package io.github.sakurawald.module.initializer.command_toolbox.send_message;

import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;

public class SendMessageInitializer extends ModuleInitializer {

    @CommandNode("send-message")
    @CommandPermission(level = 4)
    int sendMessage(ServerPlayerEntity player, GreedyString rest) {
        player.sendMessage(MessageHelper.ofText(player, false, rest.getString()));
        return CommandHelper.Return.SUCCESS;
    }

}
