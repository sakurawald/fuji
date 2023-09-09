package fun.sakurawald.mixin.chat_style;

import fun.sakurawald.ModMain;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.command.argument.MessageArgumentType.getSignedMessage;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkManagerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onChatMessage", at = @At("HEAD"))
    private void fun(ChatMessageC2SPacket packet, CallbackInfo ci) {
//        StyledChatUtils.modifyForSending(signedMessage, this.player.getCommandSource(), MessageType.CHAT);
        String s = packet.chatMessage();
        ModMain.LOGGER.info("message = " + s);
        ci.cancel();

//        try {
//            SignedMessage signedMessage = getSignedMessage(packet, (LastSeenMessageList) optional.get());
//            server.getPlayerManager().broadcast(signedMessage.withUnsignedContent(
//                    changedText
//            ), player, MessageType.params(MessageType.CHAT, player));
//        } catch (MessageChain.MessageChainException e) {
//            handleMessageChainException(e);
//        }

    }
}
