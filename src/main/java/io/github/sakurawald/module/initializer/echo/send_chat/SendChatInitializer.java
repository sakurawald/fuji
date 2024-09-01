package io.github.sakurawald.module.initializer.echo.send_chat;

import io.github.sakurawald.auxiliary.RandomUtil;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandRequirement;
import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.Instant;

public class SendChatInitializer extends ModuleInitializer {

    @CommandNode("send-chat")
    @CommandRequirement(level = 4)
    int sendChat(ServerPlayerEntity player, GreedyString message) {
        SignedMessage signedMessage = SignedMessage.ofUnsigned(message.getString());
        player.networkHandler.handleDecoratedMessage(signedMessage);
        return CommandHelper.Return.SUCCESS;
    }

}

