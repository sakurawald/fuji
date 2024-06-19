package io.github.sakurawald.module.mixin._internal.event;

import io.github.sakurawald.common.event.PrePlayerDeathEvent;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    public void $onDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        ActionResult result = PrePlayerDeathEvent.EVENT.invoker().interact(player, damageSource);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}
