package io.github.sakurawald.module.initializer.realname;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;


public class RealnameInitializer extends ModuleInitializer {

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("realname").then(argument(CommandUtil.ARGUMENT_NAME_PLYAER, EntityArgumentType.player()).executes(this::$realname)));
    }

    // huh
    private int $realname(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> CommandUtil.acceptPlayer(ctx, target -> {
            MessageUtil.sendMessage(player, "realname", target.getGameProfile().getName());
            return Command.SINGLE_SUCCESS;
        }));
    }

}
