package io.github.sakurawald.module.initializer.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;


public class ConfigModule extends ModuleInitializer {


    @Override
    public void onReload() {
        Configs.configHandler.loadFromDisk();
    }

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("fuji").requires(source -> source.hasPermission(4)).then(
                        Commands.literal("reload").executes(this::$reload)
                )
        );
    }

    private int $reload(CommandContext<CommandSourceStack> ctx) {
        // reload modules
        ModuleManager.reloadModules();

        MessageUtil.sendMessage(ctx.getSource(), "reload");
        return Command.SINGLE_SUCCESS;
    }

}
