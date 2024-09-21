package io.github.sakurawald.module.initializer.command_meta.for_each;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.core.service.command_executor.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ForEachInitializer extends ModuleInitializer {

    @CommandNode("foreach")
    @CommandRequirement(level = 4)
    private static int foreach(GreedyString rest) {
        String $rest = rest.getValue();
        MinecraftServer server = ServerHelper.getDefaultServer();

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            CommandExecutor.executeCommandAsConsole(player, $rest);
        }
        return CommandHelper.Return.SUCCESS;
    }
}
