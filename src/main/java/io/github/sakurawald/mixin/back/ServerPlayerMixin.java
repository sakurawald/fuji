package io.github.sakurawald.mixin.back;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.back.BackModule;
import io.github.sakurawald.module.teleport_warmup.TeleportWarmupModule;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
@Slf4j
public abstract class ServerPlayerMixin {


    @Unique
    private static final BackModule module = ModuleManager.getOrNewInstance(BackModule.class);
    @Unique
    private static final TeleportWarmupModule teleportWarmupModule = ModuleManager.getOrNewInstance(TeleportWarmupModule.class);

    @Inject(method = "die", at = @At("HEAD"))
    public void die(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        module.updatePlayer(player);
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", at = @At("HEAD"))
    public void $teleportTo(ServerLevel targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        // note: if TeleportWarmupModule don't update back-position for us, we do it ourselves.
        if (teleportWarmupModule == null) {
            module.updatePlayer(player);
        }
    }

}
