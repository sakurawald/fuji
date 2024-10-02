package io.github.sakurawald.module.initializer.command_meta.run;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.core.command.executor.CommandExecutor;
import io.github.sakurawald.core.command.structure.ExtendedCommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.network.ServerPlayerEntity;

@CommandNode("run")
@CommandRequirement(level =  4)
public class RunInitializer extends ModuleInitializer {

    @CommandNode("as console")
    private static int runAsConsole(GreedyString rest) {
        CommandExecutor.executeAsConsole(ExtendedCommandSource.of(), rest.getValue());
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("as player")
    private static int runAsPlayer(ServerPlayerEntity player, GreedyString rest) {
        return CommandExecutor.executeAsPlayer(ExtendedCommandSource.of(player), rest.getValue());
    }

    @CommandNode("as fake-op")
    private static int runAsFakeOp(ServerPlayerEntity player, GreedyString rest) {
        return CommandExecutor.executeAsFakeOp(ExtendedCommandSource.of(player), rest.getValue());
    }
}
