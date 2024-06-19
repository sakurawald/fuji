package io.github.sakurawald.module.mixin._internal.event;

import io.github.sakurawald.common.event.PostPlayerConnectEvent;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect", cancellable = true)
    private void $onPlayerConnect(ClientConnection connection, ServerPlayerEntity serverPlayer, ConnectedClientData commonListenerCookie, CallbackInfo ci) {
        ActionResult result = PostPlayerConnectEvent.EVENT.invoker().interact(connection, serverPlayer, commonListenerCookie);

        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}
