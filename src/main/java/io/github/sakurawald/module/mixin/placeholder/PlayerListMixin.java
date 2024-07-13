package io.github.sakurawald.module.mixin.placeholder;

import io.github.sakurawald.module.initializer.placeholder.MainStats;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerListMixin {

    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void $onPlayerConnect(ClientConnection connection, ServerPlayerEntity serverPlayer, ConnectedClientData commonListenerCookie, CallbackInfo ci) {
        String uuid = serverPlayer.getUuid().toString();
        MainStats stats = MainStats.calculatePlayerMainStats(uuid);
        MainStats.uuid2stats.put(uuid, stats);
    }
}
