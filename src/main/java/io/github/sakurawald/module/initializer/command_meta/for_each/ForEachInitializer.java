package io.github.sakurawald.module.initializer.command_meta.for_each;

import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandRequirement;
import io.github.sakurawald.module.common.service.command_executor.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.ServerHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ForEachInitializer extends ModuleInitializer {

    @CommandNode("foreach")
    @CommandRequirement(level = 4)
    private int foreach(GreedyString rest) {
        String $rest = rest.getString();
        MinecraftServer server = ServerHelper.getDefaultServer();

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            CommandExecutor.executeCommandAsConsole(player, $rest);
        }
        return CommandHelper.Return.SUCCESS;
    }
}
