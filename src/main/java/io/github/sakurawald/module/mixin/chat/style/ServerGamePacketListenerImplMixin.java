package io.github.sakurawald.module.mixin.chat.style;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.module.initializer.chat.style.ChatStyleInitializer;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 1000 + 250)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayerEntity player;

    @WrapOperation(method = "handleDecoratedMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V"))
    public void modifyChatMessageSentByPlayers(PlayerManager instance, SignedMessage signedMessage, ServerPlayerEntity serverPlayerEntity, MessageType.Parameters parameters, Operation<Void> original) {
        /* make content text */
        String contentString = signedMessage.getContent().getString();
        if (ChatStyleInitializer.config.model().spy.output_unparsed_message_into_console) {
            LogUtil.info("[chat spy] <{}> {}", player.getGameProfile().getName(), contentString);
        }

        Text contentText = ChatStyleInitializer.parseContent(player, contentString);
        signedMessage = signedMessage.withUnsignedContent(contentText);

        /* make sender text*/
        Text senderText = ChatStyleInitializer.parseSender(player);
        MessageType.Parameters senderParams = MessageType.params(ChatStyleInitializer.MESSAGE_TYPE_KEY, ServerHelper.getDefaultServer().getRegistryManager(), senderText);

        LogUtil.debug("sender = {}\n\n content = {}", senderText, contentText);

        /* send */
        ServerHelper.getPlayerManager().broadcast(signedMessage, player, senderParams);
    }
}
