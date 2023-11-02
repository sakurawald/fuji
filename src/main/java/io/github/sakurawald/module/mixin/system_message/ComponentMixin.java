package io.github.sakurawald.module.mixin.system_message;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static io.github.sakurawald.Fuji.log;

@Mixin(Component.class)
public interface ComponentMixin {

    @Inject(method = "translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;", at = @At("RETURN"), cancellable = true)
    private static void translatable(String key, Object[] args, CallbackInfoReturnable<MutableComponent> cir) {
        MutableComponent newValue = transform(key, args);
        if (newValue != null) cir.setReturnValue(newValue);
    }

    @Inject(method = "translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;", at = @At("RETURN"), cancellable = true)
    private static void translatable(String key, CallbackInfoReturnable<MutableComponent> cir) {
        MutableComponent newValue = transform(key);
        if (newValue != null) cir.setReturnValue(newValue);
    }

    @Unique
    private static @Nullable MutableComponent transform(String key, Object... args) {
        Map<String, String> key2value = ConfigManager.configWrapper.instance().modules.system_message.key2value;
        if (key2value.containsKey(key)) {
            if (Fuji.SERVER == null) {
                log.warn("Server is null currently -> cannot hijack message key: {}", key);
                return null;
            }
            String value = key2value.get(key);
            String miniMessageSource = MutableComponent.create(new TranslatableContents("force_fallback", value, args)).getString();
            return MessageUtil.ofVomponent(miniMessageSource).copy();
        }
        return null;
    }
}
