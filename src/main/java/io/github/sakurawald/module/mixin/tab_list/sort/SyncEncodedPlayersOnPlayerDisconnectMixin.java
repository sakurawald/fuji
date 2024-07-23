package io.github.sakurawald.module.mixin.tab_list.sort;


import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.tab_list.sort.TabListSortInitializer;
import io.netty.util.concurrent.CompleteFuture;
import lombok.extern.slf4j.Slf4j;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(ServerPlayNetworkHandler.class)
@Slf4j
public class SyncEncodedPlayersOnPlayerDisconnectMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(at = @At("TAIL"), method = "onDisconnected")
    private void removeEncodedPlayerFromTabList(DisconnectionInfo disconnectionInfo, CallbackInfo ci) {
        CompletableFuture.runAsync(() -> {
            String encodedName = TabListSortInitializer.encodeName(player);
            PlayerRemoveS2CPacket playerRemoveS2CPacket = new PlayerRemoveS2CPacket(List.of(UUID.nameUUIDFromBytes(encodedName.getBytes())));
            Fuji.SERVER.getPlayerManager().sendToAll(playerRemoveS2CPacket);
        });
    }
}
