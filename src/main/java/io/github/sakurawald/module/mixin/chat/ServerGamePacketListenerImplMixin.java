package io.github.sakurawald.module.mixin.chat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.module.initializer.chat.ChatInitializer;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 9999)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayerEntity player;

    @WrapOperation(method = "handleDecoratedMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V"))
    public void modifyChatMessageSentByPlayers(PlayerManager instance, SignedMessage signedMessage, ServerPlayerEntity serverPlayerEntity, MessageType.Parameters parameters, Operation<Void> original) {
        // visit the node and build the string.
        String string = signedMessage.getContent().getString();
        if (ChatInitializer.config.getModel().spy.output_unparsed_message_into_console) {
            LogUtil.info("[chat spy] <{}> {}", player.getGameProfile().getName(), string);
        }

        // parse
        Text text = ChatInitializer.parseText(player, string);
        signedMessage = signedMessage.withUnsignedContent(text);
        ServerHelper.getPlayerManager().broadcast(signedMessage, player, MessageType.params(ChatInitializer.MESSAGE_TYPE_KEY, player));
    }
}
