package io.github.sakurawald.module.initializer.command_toolbox.help_op;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class HelpOpInitializer extends ModuleInitializer {

    @Command("helpop")
    int helpop(@CommandSource ServerPlayerEntity player, GreedyString message) {
        PlayerManager playerManager = ServerHelper.getDefaultServer().getPlayerManager();

        List<ServerPlayerEntity> ops = playerManager.getPlayerList().stream().filter(p -> playerManager.isOperator(p.getGameProfile())).toList();

        if (ops.isEmpty()) {
            MessageHelper.sendMessage(player,"helpop.fail");
            return CommandHelper.Return.FAIL;
        }

        Text text = MessageHelper.ofText(player, "helpop.format", player.getGameProfile().getName(), message.getString());
        ops.forEach(o -> o.sendMessage(text));

        MessageHelper.sendMessage(player,"helpop.success");
        return CommandHelper.Return.SUCCESS;
    }

}