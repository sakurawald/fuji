package io.github.sakurawald.module.initializer.command_toolbox.send_actionbar;

import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandRequirement;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;

public class SendActionbar extends ModuleInitializer {

    @CommandNode("send-actionbar")
    @CommandRequirement(level = 4)
    int sendActionBar(ServerPlayerEntity player, GreedyString rest) {
        player.sendActionBar(MessageHelper.ofText(player, false, rest.getString()));
        return CommandHelper.Return.SUCCESS;
    }

}
