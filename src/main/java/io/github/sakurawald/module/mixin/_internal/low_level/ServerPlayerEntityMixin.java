package io.github.sakurawald.module.mixin._internal.low_level;

import io.github.sakurawald.module.common.accessor.ServerPlayerCombatStateAccessor;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements ServerPlayerCombatStateAccessor {

    @Unique
    public boolean fuji$inCombat;

    @Inject(method = "enterCombat", at = @At("RETURN"))
    public void $enterCombat(CallbackInfo ci) {
        fuji$inCombat = true;
    }

    @Inject(method = "endCombat", at = @At("RETURN"))
    public void $leaveCombat(CallbackInfo ci) {
        fuji$inCombat = false;
    }

    @Override
    public boolean fuji$inCombat() {
        return fuji$inCombat;
    }

}
