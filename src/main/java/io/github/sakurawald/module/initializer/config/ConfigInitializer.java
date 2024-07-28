package io.github.sakurawald.module.initializer.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;


public class ConfigInitializer extends ModuleInitializer {


    @Override
    public void onReload() {
        Configs.configHandler.loadFromDisk();
    }

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("fuji").requires(source -> source.hasPermissionLevel(4)).then(
                        CommandManager.literal("reload").executes(this::$reload)
                )
        );
    }

    private int $reload(@NotNull CommandContext<ServerCommandSource> ctx) {
        // reload modules
        Managers.getModuleManager().reloadModules();

        // reload languages
        MessageHelper.getLang2json().clear();

        MessageHelper.sendMessage(ctx.getSource(), "reload");
        return CommandHelper.Return.SUCCESS;
    }

}
