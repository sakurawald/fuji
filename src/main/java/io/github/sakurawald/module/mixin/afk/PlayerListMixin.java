package io.github.sakurawald.module.mixin.afk;

import io.github.sakurawald.module.initializer.afk.ServerPlayerAccessor_afk;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)

public abstract class PlayerListMixin {
    @Inject(at = @At(value = "TAIL"), method = "placeNewPlayer")
    private void $placeNewPlayer(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        ServerPlayerAccessor_afk afk_player = (ServerPlayerAccessor_afk) serverPlayer;
        afk_player.fuji$setLastLastActionTime(serverPlayer.getLastActionTime());
    }
}