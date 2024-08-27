package io.github.sakurawald.module.mixin.afk.effect;

import com.mojang.authlib.GameProfile;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.afk.accessor.AfkStateAccessor;
import io.github.sakurawald.module.initializer.pvp.PvpInitializer;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Unique
    ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void $invulnerableEffect(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        AfkStateAccessor afkStateAccessor = (AfkStateAccessor) player;
        if (afkStateAccessor.fuji$isAfk()) {
            cir.setReturnValue(false);
        }
    }
}
