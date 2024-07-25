package io.github.sakurawald.module.initializer.command_meta.run;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.common.structure.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import lombok.SneakyThrows;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

public class RunInitializer extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("run")
                .then(literal("as")
                        .then(literal("player").then(CommandHelper.Argument.player().then(CommandHelper.Argument.rest().executes(this::runAsPlayer))))
                        .then(literal("console").then(CommandHelper.Argument.rest().executes(this::runAsConsole)))));

    }

    private int runAsConsole(CommandContext<ServerCommandSource> ctx) {
        String rest = CommandHelper.Argument.rest(ctx);

        CommandExecutor.executeCommandAsConsole(null, rest);
        return CommandHelper.Return.SUCCESS;
    }

    @SneakyThrows
    private int runAsPlayer(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = CommandHelper.Argument.player(ctx);
        String rest = CommandHelper.Argument.rest(ctx);

        CommandExecutor.executeCommandAsPlayer(player, rest);
        return CommandHelper.Return.SUCCESS;
    }
}
