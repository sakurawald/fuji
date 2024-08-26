package io.github.sakurawald.module.initializer.command_meta.run;

import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.module.common.service.command_executor.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import net.minecraft.server.network.ServerPlayerEntity;

@CommandNode("run")
@CommandPermission(level =  4)
public class RunInitializer extends ModuleInitializer {

    @CommandNode("as console")
    private int runAsConsole(GreedyString rest) {
        CommandExecutor.executeCommandAsConsole(null, rest.getString());
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("as player")
    private int runAsPlayer(ServerPlayerEntity player, GreedyString rest) {
        return CommandExecutor.executeCommandAsPlayer(player, rest.getString());
    }

    @CommandNode("as fake-op")
    private int runAsFakeOp(ServerPlayerEntity player, GreedyString rest) {
        return CommandExecutor.executeCommandAsFakeOp(player, rest.getString());
    }
}
