package io.github.sakurawald.module.initializer.command_toolbox.suicide;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;


public class SuicideInitializer extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("suicide").executes(this::$suicide));
    }

    @SuppressWarnings("SameReturnValue")
    private int $suicide(CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.playerOnlyCommand(ctx, player -> {
            player.kill();
            return CommandHelper.Return.SUCCESS;
        });
    }


}
