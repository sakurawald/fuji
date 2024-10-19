package io.github.sakurawald.module.mixin.color.anvil;

import io.github.sakurawald.core.auxiliary.minecraft.TextHelper;
import net.minecraft.screen.AnvilScreenHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {

    @Shadow
    private String newItemName;

    @ModifyArg(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;set(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    public @NotNull Object updateResult(Object text) {
        return TextHelper.getTextByValue(null, newItemName);
    }

    @ModifyArg(method = "setNewItemName", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;set(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    public @NotNull Object newItemName(Object text) {
        return TextHelper.getTextByValue(null, newItemName);
    }
}
