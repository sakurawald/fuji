package io.github.sakurawald.module.mixin.multiplier;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.sakurawald.util.LuckPermsUtil;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
@Slf4j
public abstract class ServerPlayerMixin {

    @Unique
    ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @Unique
    float transform(ServerPlayerEntity player, String type, String key, float f) {
        Optional<Float> meta = LuckPermsUtil.getMeta(player, "fuji.multiplier.%s.%s".formatted(type, key), Float::valueOf);
        return meta.map(factor -> f * factor).orElse(f);
    }

    @ModifyVariable(method = "damage", at = @At(value = "HEAD"), argsOnly = true)
    public float transformDamage(float damage, @Local(argsOnly = true) DamageSource damageSource) {
        damage = transform(player, "damage", "all", damage);
        damage = transform(player, "damage", damageSource.getTypeRegistryEntry().getIdAsString(), damage);
        return damage;
    }

    @ModifyVariable(method = "addExperience", at = @At(value = "HEAD"), argsOnly = true)
    public int transformExperience(int exp) {
        exp = (int) transform(player, "experience", "all", exp);
        return exp;
    }

}
