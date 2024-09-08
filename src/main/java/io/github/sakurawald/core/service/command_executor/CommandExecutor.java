package io.github.sakurawald.core.service.command_executor;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LanguageHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;


public class CommandExecutor {

    public static void executeSpecializedCommand(@Nullable ServerPlayerEntity contextPlayer, @NotNull List<String> commands) {
        /* context
         *
         * !as_console
         * !as_player
         * !as_fake_op
         *
         *
         *  */
        // ... to write

        /* resolve */
        for (String command : commands) {
            executeCommandAsConsole(contextPlayer, command);
        }
    }

    public static int executeCommandAsConsole(@Nullable ServerPlayerEntity contextPlayer, String command) {
        MinecraftServer server = ServerHelper.getDefaultServer();
        try {
            // parse placeholders
            if (contextPlayer != null) {
                command = LanguageHelper.resolvePlaceholder(contextPlayer, command);
            } else {
                command = LanguageHelper.resolvePlaceholder(ServerHelper.getDefaultServer(), command);
            }

            return server.getCommandManager().getDispatcher().execute(command, server.getCommandSource());
        } catch (CommandSyntaxException e) {
            LogUtil.cryLoudly("CommandExecuter fails to execute commands.", e);
        }
        return CommandHelper.Return.FAIL;
    }


    public static int executeCommandAsPlayer(@NotNull ServerPlayerEntity player, String command, Function<ServerCommandSource, ServerCommandSource> source) {
        command = LanguageHelper.resolvePlaceholder(player, command);

        CommandManager commandManager = ServerHelper.getDefaultServer().getCommandManager();
        CommandDispatcher<ServerCommandSource> dispatcher = commandManager.getDispatcher();

        ServerCommandSource serverCommandSource = source.apply(player.getCommandSource());

        ParseResults<ServerCommandSource> parseResults
                = dispatcher.parse(command, serverCommandSource);
        try {
            return dispatcher.execute(parseResults);
        } catch (CommandSyntaxException e) {
            LogUtil.cryLoudly("CommandExecuter fails to execute commands.", e);
        }

        return CommandHelper.Return.FAIL;
    }

    public static int executeCommandAsPlayer(@NotNull ServerPlayerEntity player, String command) {
       return executeCommandAsPlayer(player, command, (source) -> source);
    }

    public static int executeCommandAsFakeOp(@NotNull ServerPlayerEntity player, String command) {
        return executeCommandAsPlayer(player, command, (source) -> source.withLevel(4));
    }
}
