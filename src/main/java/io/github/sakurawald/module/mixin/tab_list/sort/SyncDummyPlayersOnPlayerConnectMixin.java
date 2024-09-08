package io.github.sakurawald.module.mixin.tab_list.sort;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.sakurawald.module.initializer.tab_list.sort.structure.TabListEntry;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(PlayerManager.class)
// https://wiki.vg/Protocol#Player_Info_Remove
public abstract class SyncDummyPlayersOnPlayerConnectMixin {

    @Shadow
    public abstract void sendToAll(Packet<?> packet);

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 5, shift = At.Shift.AFTER))
    void sendDummyTabListToNewPlayer(ClientConnection clientConnection, @NotNull ServerPlayerEntity serverPlayerEntity, ConnectedClientData connectedClientData, CallbackInfo ci) {
        CompletableFuture.runAsync(() -> {
            // note: at the time point, the new joined player still don't put into the PlayerManager's players list.
            serverPlayerEntity.networkHandler.sendPacket(TabListEntry.makePacket(TabListEntry.getDummyPlayerList()));
        });
    }

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0, shift = At.Shift.AFTER))
    void sendDummyNewJoinedPlayerToOtherPlayers(ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity, ConnectedClientData connectedClientData, CallbackInfo ci, @Local(argsOnly = true) ServerPlayerEntity newJoinedPlayer) {
        CompletableFuture.runAsync(() -> {
            ServerPlayerEntity dummyPlayer = TabListEntry.getEntryFromRealPlayer(newJoinedPlayer).getDummyPlayer();

            sendToAll(TabListEntry.makePacket(List.of(dummyPlayer)));
        });
    }

}
