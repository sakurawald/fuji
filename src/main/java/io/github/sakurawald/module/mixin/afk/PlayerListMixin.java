package io.github.sakurawald.module.mixin.afk;

import io.github.sakurawald.module.initializer.afk.accessor.AfkStateAccessor;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerListMixin {
    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void $onPlayerConnect(ClientConnection connection, @NotNull ServerPlayerEntity serverPlayer, ConnectedClientData commonListenerCookie, CallbackInfo ci) {
        AfkStateAccessor afk_player = (AfkStateAccessor) serverPlayer;
        afk_player.fuji$setLastLastActionTime(serverPlayer.getLastActionTime());
    }
}