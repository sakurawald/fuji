package io.github.sakurawald.module.common.structure;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;


@Slf4j
public class CommandExecuter {

    public static void executeCommandsAsConsoleWithContext(ServerPlayerEntity contextPlayer, List<String> commands) {
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

    public static void executeCommandAsConsole(ServerPlayerEntity contextPlayer, String command) {
        MinecraftServer server = Fuji.SERVER;
        try {
            // parse placeholders
            if (contextPlayer != null) {
                command = MessageUtil.ofString(contextPlayer,command);
            } else {
                command = MessageUtil.ofString(Fuji.SERVER,command);
            }

            server.getCommandManager().getDispatcher().execute(command, server.getCommandSource());
        } catch (CommandSyntaxException e) {
            Fuji.LOGGER.error(e.toString());
        }
    }

    public static void executeCommandAsPlayer(ServerPlayerEntity player, String command) {
        command = MessageUtil.ofString(player, command);
        player.networkHandler.onCommandExecution(new CommandExecutionC2SPacket(command));
    }

}
