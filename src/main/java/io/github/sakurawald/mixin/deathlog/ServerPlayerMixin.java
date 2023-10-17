package io.github.sakurawald.mixin.deathlog;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.deathlog.DeathLogModule;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Unique
    private static final DeathLogModule module = ModuleManager.getOrNewInstance(DeathLogModule.class);

    @Inject(method = "die", at = @At("HEAD"))
    public void die(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        // don't store empty inventory
        if (player.getInventory().isEmpty()) return;
        module.store(player);
    }

}
