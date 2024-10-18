package io.github.sakurawald.module.mixin.chat.style.message_type;

import io.github.sakurawald.module.initializer.chat.style.ChatStyleInitializer;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.Registerable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageType.class)
public class MessageTypeMixin {

    @Inject(method = "bootstrap", at = @At("HEAD"))
    private static void registerNewMessageType(Registerable<MessageType> registerable, CallbackInfo ci) {
        registerable.register(ChatStyleInitializer.MESSAGE_TYPE_KEY, ChatStyleInitializer.MESSAGE_TYPE_VALUE);
    }
}
