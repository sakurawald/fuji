package io.github.sakurawald.core.service.command_executor;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;


@UtilityClass
public class CommandExecutor {

    public static void executeSpecializedCommand(@Nullable PlayerEntity contextPlayer, @NotNull List<String> commands) {
        /* resolve */
        for (String command : commands) {
            executeCommandAsConsole(contextPlayer, command);
        }
    }

    public static int executeCommandAsConsole(@Nullable PlayerEntity contextPlayer, String command) {
        MinecraftServer server = ServerHelper.getDefaultServer();
        try {
            // parse placeholders
            if (contextPlayer != null) {
                command = LocaleHelper.resolvePlaceholder(contextPlayer, command);
            } else {
                command = LocaleHelper.resolvePlaceholder(ServerHelper.getDefaultServer(), command);
            }

            return ServerHelper.getCommandDispatcher().execute(command, server.getCommandSource());
        } catch (CommandSyntaxException e) {
            LogUtil.error("CommandExecuter fails to execute the command: /{}", command, e);
        }
        return CommandHelper.Return.FAIL;
    }


    @SneakyThrows
    public static int executeCommandAsPlayer(@NotNull PlayerEntity player, String command, Function<ServerCommandSource, ServerCommandSource> source) {
        command = LocaleHelper.resolvePlaceholder(player, command);

        CommandDispatcher<ServerCommandSource> dispatcher = ServerHelper.getCommandDispatcher();

        ServerCommandSource serverCommandSource = source.apply(player.getCommandSource());
        ParseResults<ServerCommandSource> parseResults
            = dispatcher.parse(command, serverCommandSource);

        try {
            return dispatcher.execute(parseResults);
        } catch (CommandSyntaxException e) {
            player.sendMessage(Text
                .literal("During the execution of: /" + command)
                .append(LocaleHelper.TEXT_NEWLINE)
                .append(Text.literal("Exception: " + e.getMessage()))
                .formatted(Formatting.RED));
        }

        return CommandHelper.Return.FAIL;
    }

    public static int executeCommandAsPlayer(@NotNull PlayerEntity player, String command) {
        return executeCommandAsPlayer(player, command, (source) -> source);
    }

    public static int executeCommandAsFakeOp(@NotNull PlayerEntity player, String command) {
        return executeCommandAsPlayer(player, command, (source) -> source.withLevel(4));
    }
}
