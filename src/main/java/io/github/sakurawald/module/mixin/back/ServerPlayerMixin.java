package io.github.sakurawald.module.mixin.back;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.back.BackInitializer;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(value = ServerPlayerEntity.class)
public abstract class ServerPlayerMixin {

    @Unique
    @NotNull
    ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @Unique
    private static final BackInitializer module = ModuleManager.getInitializer(BackInitializer.class);

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void $onDeath(DamageSource damageSource, CallbackInfo ci) {
        module.saveCurrentPosition(player);
    }

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V", at = @At("HEAD"))
    public void teleport(ServerWorld serverWorld, double d, double e, double f, float g, float h, CallbackInfo ci) {
        module.saveCurrentPosition(player);
    }

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDLjava/util/Set;FF)Z", at = @At("HEAD"))
    public void teleport(ServerWorld serverWorld, double d, double e, double f, Set<PositionFlag> set, float g, float h, CallbackInfoReturnable<Boolean> cir) {
        module.saveCurrentPosition(player);
    }

}
