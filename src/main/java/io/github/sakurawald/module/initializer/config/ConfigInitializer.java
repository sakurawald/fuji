package io.github.sakurawald.module.initializer.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
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


@Command("fuji")
@CommandPermission(level = 4)
public class ConfigInitializer extends ModuleInitializer {


    @Override
    public void onReload() {
        Configs.configHandler.loadFromDisk();
    }

    @Command("reload")
    private int $reload(@CommandSource CommandContext<ServerCommandSource> ctx) {
        // reload modules
        Managers.getModuleManager().reloadModules();

        // reload languages
        MessageHelper.getLang2json().clear();

        MessageHelper.sendMessage(ctx.getSource(), "reload");
        return CommandHelper.Return.SUCCESS;
    }

}
