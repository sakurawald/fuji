package io.github.sakurawald.module.mixin.deathlog;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.deathlog.DeathLogInitializer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin {
    @Unique
    private static final DeathLogInitializer module = ModuleManager.getInitializer(DeathLogInitializer.class);

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void $onDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        // don't store empty inventory
        if (player.getInventory().isEmpty()) return;
        module.store(player);
    }

}
