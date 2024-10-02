package io.github.sakurawald.core.command.executor;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.command.structure.ExtendedCommandSource;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@UtilityClass
public class CommandExecutor {

    public static void executeAsConsole(@NotNull ExtendedCommandSource context, @NotNull List<String> commands) {
        commands.forEach(command -> executeAsConsole(context, command));
    }

    public static int executeAsConsole(@NotNull ExtendedCommandSource context, String command) {
        try {
            // pre process
            command = context.processCommand(command);

            return ServerHelper.getCommandDispatcher().execute(command, ServerHelper.getDefaultServer().getCommandSource());
        } catch (CommandSyntaxException e) {
            LogUtil.error("CommandExecuter fails to execute the command: /{}", command, e);
        }
        return CommandHelper.Return.FAIL;
    }

    @SneakyThrows
    public static int executeAsPlayer(@NotNull ExtendedCommandSource context, String command) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            LogUtil.warn("failed to execute the command {} as a player: the contextual player is null.", command);
            return CommandHelper.Return.FAIL;
        }

        command = context.processCommand(command);
        CommandDispatcher<ServerCommandSource> dispatcher = ServerHelper.getCommandDispatcher();
        ParseResults<ServerCommandSource> parseResults = dispatcher.parse(command, context.getSource());
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

    public static int executeAsFakeOp(@NotNull ExtendedCommandSource context, String command) {
        return executeAsPlayer(context.modifySource(s -> s.withLevel(4)), command);
    }
}
