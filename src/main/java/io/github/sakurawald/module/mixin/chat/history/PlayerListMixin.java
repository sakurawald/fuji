package io.github.sakurawald.module.mixin.chat.history;

import io.github.sakurawald.core.auxiliary.minecraft.EntityHelper;
import io.github.sakurawald.module.initializer.chat.history.ChatHistoryInitializer;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(value = PlayerManager.class, priority = 999)
public abstract class PlayerListMixin {

    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void sendChatHistoryToNewJoinedPlayer(ClientConnection connection, @NotNull ServerPlayerEntity serverPlayer, ConnectedClientData commonListenerCookie, CallbackInfo ci) {
        if (EntityHelper.isNonRealPlayer(serverPlayer)) return;
        ChatHistoryInitializer.getChatHistory().forEach(serverPlayer::sendMessage);
    }

    @Inject(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V"
        , at = @At(value = "TAIL"))
    void f(SignedMessage signedMessage, Predicate<ServerPlayerEntity> predicate, @Nullable ServerPlayerEntity serverPlayerEntity, MessageType.Parameters parameters, CallbackInfo ci) {
        Text decoratedText = parameters.applyChatDecoration(signedMessage.getContent());
        ChatHistoryInitializer.getChatHistory().add(decoratedText);
    }
}
