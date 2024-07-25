package io.github.sakurawald.module.common.structure;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;


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

    public static int executeCommandAsConsole(ServerPlayerEntity contextPlayer, String command) {
        MinecraftServer server = ServerHelper.getDefaultServer();
        try {
            // parse placeholders
            if (contextPlayer != null) {
                command = MessageHelper.ofString(contextPlayer, command);
            } else {
                command = MessageHelper.ofString(ServerHelper.getDefaultServer(), command);
            }

            return server.getCommandManager().getDispatcher().execute(command, server.getCommandSource());
        } catch (CommandSyntaxException e) {
            LogUtil.cryLoudly("CommandExecuter fails to execute commands.", e);
        }
        return CommandHelper.Return.FAIL;
    }

    public static int executeCommandAsPlayer(ServerPlayerEntity player, String command) {
        command = MessageHelper.ofString(player, command);

        CommandManager commandManager = ServerHelper.getDefaultServer().getCommandManager();
        CommandDispatcher<ServerCommandSource> dispatcher = commandManager.getDispatcher();
        ParseResults<ServerCommandSource> parseResults
                = dispatcher.parse(command, player.getCommandSource());
        try {
            return dispatcher.execute(parseResults);
        } catch (CommandSyntaxException e) {
            LogUtil.cryLoudly("CommandExecuter fails to execute commands.", e);
        }

        return CommandHelper.Return.FAIL;
    }

}
