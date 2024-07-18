package io.github.sakurawald.module.common.structure;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Random;


@Slf4j
public class SpecializedCommand {

    private static final String RANDOM_PLAYER = "!random_player!";
    private static final String ALL_PLAYER = "!all_player!";

    public static void runSpecializedCommands(MinecraftServer server, List<String> commands) {

        /* context */
        String randomPlayer = null;
        String[] onlinePlayers = server.getPlayerNames();

        /* resolve */
        for (String command : commands) {
            /* resolve random player */
            if (command.contains(RANDOM_PLAYER)) {
                if (randomPlayer == null) {
                    randomPlayer = onlinePlayers[new Random().nextInt(onlinePlayers.length)];
                }
                command = command.replace(RANDOM_PLAYER, randomPlayer);
            }

            /* resolve all players */
            if (command.contains(ALL_PLAYER)) {
                for (String onlinePlayer : onlinePlayers) {
                    executeCommand(server, command.replace(ALL_PLAYER, onlinePlayer));
                }
            } else {
                executeCommand(server, command);
            }
        }
    }

    public static void executeCommand(MinecraftServer server, String command) {
        try {
            server.getCommandManager().getDispatcher().execute(command, server.getCommandSource());
        } catch (CommandSyntaxException e) {
            Fuji.LOGGER.error(e.toString());
        }
    }

    public static void executeCommands(ServerPlayerEntity player, List<String> commands) {
        commands.forEach(command -> executeCommand(player, command));
    }

    public static void executeCommand(ServerPlayerEntity player, String command) {
        command = MessageUtil.ofString(player, false, command);
        player.networkHandler.onCommandExecution(new CommandExecutionC2SPacket(command));
    }

}
