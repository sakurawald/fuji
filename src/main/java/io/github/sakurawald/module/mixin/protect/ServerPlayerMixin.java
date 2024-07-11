package io.github.sakurawald.module.mixin.protect;

import io.github.sakurawald.config.Configs;
import lombok.extern.slf4j.Slf4j;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ServerPlayerEntity.class)
@Slf4j
public abstract class ServerPlayerMixin {

    @Inject(method = "damage", at = @At(value = "HEAD"), cancellable = true)
    public void damage(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        Set<String> damageTypeChecklist = Configs.configHandler.model().modules.protect.damage_type_checklist;

        String key = damageSource.getTypeRegistryEntry().getIdAsString();
        if (damageTypeChecklist.contains(key)) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            if (Permissions.check(player, "fuji.protect.%s".formatted(key))) {
                cir.cancel();
            }
        }
    }

}
