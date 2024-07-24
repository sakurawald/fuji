package io.github.sakurawald.module.mixin.tab_list.sort;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.sakurawald.module.initializer.tab_list.sort.TabListSortInitializer;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(PlayerManager.class)
// https://wiki.vg/Protocol#Player_Info_Remove
public abstract class SyncEncodedPlayersOnPlayerConnectMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    private List<ServerPlayerEntity> players;

    @Shadow
    public abstract void sendToAll(Packet<?> packet);

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 5, shift = At.Shift.AFTER))
    void sendEncodedTabListToNewPlayer(ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity, ConnectedClientData connectedClientData, CallbackInfo ci) {
        CompletableFuture.runAsync(() -> {
            List<ServerPlayerEntity> encodedOtherPlayers = new ArrayList<>();
            // note: at the time point, the new joined player still don't put into the PlayerManager's players list.
            for (ServerPlayerEntity player : this.players) {
                encodedOtherPlayers.add(TabListSortInitializer.makeServerPlayerEntity(server, player));
            }

            serverPlayerEntity.networkHandler.sendPacket(TabListSortInitializer.entryFromEncodedPlayer(encodedOtherPlayers));
        });
    }

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0, shift = At.Shift.AFTER))
    void sendEncodedNewJoinedPlayerToOtherPlayers(ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity, ConnectedClientData connectedClientData, CallbackInfo ci, @Local(argsOnly = true) ServerPlayerEntity newJoinedPlayer) {
        CompletableFuture.runAsync(() -> {
            ServerPlayerEntity encodedNewJoinedPlayer = TabListSortInitializer.makeServerPlayerEntity(server, newJoinedPlayer);
            sendToAll(TabListSortInitializer.entryFromEncodedPlayer(List.of(encodedNewJoinedPlayer)));
        });
    }

}
