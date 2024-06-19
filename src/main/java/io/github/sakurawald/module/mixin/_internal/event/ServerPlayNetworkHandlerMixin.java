package io.github.sakurawald.module.mixin._internal.event;


import io.github.sakurawald.common.event.PrePlayerDisconnectEvent;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(at = @At(value = "HEAD"), method = "onDisconnected", cancellable = true)
    private void $onDisconnected(DisconnectionInfo disconnectionInfo, CallbackInfo ci) {
        ActionResult result = PrePlayerDisconnectEvent.EVENT.invoker().interact(player, disconnectionInfo);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}


