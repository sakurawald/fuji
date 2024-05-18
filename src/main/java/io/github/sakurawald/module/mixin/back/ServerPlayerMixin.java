package io.github.sakurawald.module.mixin.back;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.back.BackModule;
import io.github.sakurawald.module.initializer.teleport_warmup.TeleportWarmupModule;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)

public abstract class ServerPlayerMixin {


    @Unique
    private static final BackModule module = ModuleManager.getInitializer(BackModule.class);
    @Unique
    private static final TeleportWarmupModule teleportWarmupModule = ModuleManager.getInitializer(TeleportWarmupModule.class);

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void $onDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        module.updatePlayer(player);
    }

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V", at = @At("HEAD"))
    public void $teleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // note: if TeleportWarmupModule don't update back-position for us, we do it ourselves.
        if (teleportWarmupModule == null) {
            module.updatePlayer(player);
        }
    }

}
