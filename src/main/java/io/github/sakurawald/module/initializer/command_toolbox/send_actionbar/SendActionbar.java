package io.github.sakurawald.module.initializer.command_toolbox.send_actionbar;

import com.mojang.brigadier.CommandDispatcher;
import io.github.sakurawald.command.adapter.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.literal;

public class SendActionbar extends ModuleInitializer {

    @Command("sendactionbar")
    @CommandPermission(level = 4)
    int sendActionBar(ServerPlayerEntity player, GreedyString rest) {
        player.sendActionBar(MessageHelper.ofText(player, false, rest.getString()));
        return CommandHelper.Return.SUCCESS;
    }

}
