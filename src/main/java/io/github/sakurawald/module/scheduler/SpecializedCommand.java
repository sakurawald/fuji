package io.github.sakurawald.module.scheduler;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Random;

@Slf4j
public class SpecializedCommand {

    // TODO: a language parser is needed here (supports some expressions solver)

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

            /* resolve all player */
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
            server.getCommands().getDispatcher().execute(command, server.createCommandSourceStack());
        } catch (CommandSyntaxException e) {
            log.error(e.toString());
        }
    }

}
