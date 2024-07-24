package io.github.sakurawald.module.initializer.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;


public class ConfigInitializer extends ModuleInitializer {


    @Override
    public void onReload() {
        Configs.configHandler.loadFromDisk();
    }

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("fuji").requires(source -> source.hasPermissionLevel(4)).then(
                        CommandManager.literal("reload").executes(this::$reload)
                )
        );
    }

    private int $reload(CommandContext<ServerCommandSource> ctx) {
        // reload modules
        ModuleManager.reloadModules();

        MessageHelper.sendMessage(ctx.getSource(), "reload");
        return CommandHelper.Return.SUCCESS;
    }

}
