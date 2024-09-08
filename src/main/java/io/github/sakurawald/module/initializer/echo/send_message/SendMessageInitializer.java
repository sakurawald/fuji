package io.github.sakurawald.module.initializer.echo.send_message;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.network.ServerPlayerEntity;

public class SendMessageInitializer extends ModuleInitializer {

    @CommandNode("send-message")
    @CommandRequirement(level = 4)
    int sendMessage(ServerPlayerEntity player, GreedyString rest) {
        player.sendMessage(MessageHelper.ofText(player, false, rest.getValue()));
        return CommandHelper.Return.SUCCESS;
    }

}
