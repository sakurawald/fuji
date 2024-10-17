package io.github.sakurawald.module.mixin.chat.history;

import io.github.sakurawald.module.initializer.chat.history.ChatHistoryInitializer;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 1000 + 1000)
public abstract class ServerPlayNetworkHandlerMixin {

    @Inject(method = "sendChatMessage", at = @At(value = "TAIL"))
    void f(SignedMessage signedMessage, MessageType.Parameters parameters, CallbackInfo ci) {
        Text decoratedTextAsTheClientSideDo = parameters.applyChatDecoration(signedMessage.getContent());
        ChatHistoryInitializer.getChatHistory().add(decoratedTextAsTheClientSideDo);
    }

}
