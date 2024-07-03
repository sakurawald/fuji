package io.github.sakurawald.module.mixin.enchantment;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.enchantment.MyEnchantmentScreenHandler;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.screen.EnchantmentScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EnchantmentScreenHandler.class)
@Slf4j
public class EnchantmentScreenHandlerMixin {

    @ModifyArg(method = "method_17411(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;calculateRequiredExperienceLevel(Lnet/minecraft/util/math/random/Random;IILnet/minecraft/item/ItemStack;)I"), index = 2)
    int injected(int i) {
        var enchantment = Configs.configHandler.model().modules.enchantment;
        if (enchantment.enable) {
            EnchantmentScreenHandler instance = ((EnchantmentScreenHandler) (Object) this);
            if (instance instanceof MyEnchantmentScreenHandler) {
                return enchantment.override_power.power_provider_amount;
            }
        }
        return i;
    }

}
