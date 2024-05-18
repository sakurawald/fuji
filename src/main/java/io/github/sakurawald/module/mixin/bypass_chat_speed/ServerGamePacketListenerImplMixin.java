package io.github.sakurawald.module.mixin.bypass_chat_speed;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "checkForSpam", at = @At("HEAD"), cancellable = true)
    public void $checkForSpam(CallbackInfo ci) {
        ci.cancel();
    }

}
