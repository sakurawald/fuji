package io.github.sakurawald.module.initializer.command_toolbox.repair;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;


public class RepairInitializer extends ModuleInitializer {

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("repair").executes(this::$repair));
    }

    @SuppressWarnings("SameReturnValue")
    private int $repair(CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.playerOnlyCommand(ctx, player -> {
            player.getMainHandStack().setDamage(0);
            MessageHelper.sendMessage(player, "repair");
            return CommandHelper.Return.SUCCESS;
        });
    }

}
