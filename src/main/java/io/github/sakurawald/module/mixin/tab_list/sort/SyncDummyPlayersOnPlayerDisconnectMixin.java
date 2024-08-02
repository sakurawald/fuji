package io.github.sakurawald.module.mixin.tab_list.sort;


import io.github.sakurawald.module.initializer.tab_list.sort.structure.TabListEntry;
import io.github.sakurawald.util.minecraft.ServerHelper;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(ServerPlayNetworkHandler.class)
public class SyncDummyPlayersOnPlayerDisconnectMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(at = @At("TAIL"), method = "onDisconnected")
    private void removeEncodedPlayerFromTabList(DisconnectionInfo disconnectionInfo, CallbackInfo ci) {
        CompletableFuture.runAsync(() -> {
            TabListEntry entry = TabListEntry.getEntryFromRealPlayer(player);
            ServerPlayerEntity dummyPlayer = entry.getDummyPlayer();
            TabListEntry.getInstances().remove(entry);

            PlayerRemoveS2CPacket playerRemoveS2CPacket = new PlayerRemoveS2CPacket(List.of(dummyPlayer.getUuid()));

            ServerHelper.getDefaultServer().getPlayerManager().sendToAll(playerRemoveS2CPacket);
        });
    }
}
