package io.github.sakurawald.module.initializer.command_toolbox.help_op;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class HelpOpInitializer extends ModuleInitializer {

    @CommandNode("helpop")
    int helpop(@CommandSource ServerPlayerEntity player, GreedyString message) {
        PlayerManager playerManager = ServerHelper.getDefaultServer().getPlayerManager();

        List<ServerPlayerEntity> ops = playerManager.getPlayerList().stream().filter(p -> playerManager.isOperator(p.getGameProfile())).toList();

        if (ops.isEmpty()) {
            LocaleHelper.sendMessageByKey(player,"helpop.fail");
            return CommandHelper.Return.FAIL;
        }

        Text text = LocaleHelper.getTextByKey(player, "helpop.format", player.getGameProfile().getName(), message.getValue());
        ops.forEach(o -> o.sendMessage(text));

        LocaleHelper.sendMessageByKey(player,"helpop.success");
        return CommandHelper.Return.SUCCESS;
    }

}
