package io.github.sakurawald.module.initializer.config;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandRequirement;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.job.abst.BaseJob;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import net.minecraft.server.command.ServerCommandSource;


@CommandNode("fuji")
@CommandRequirement(level = 4)
public class ConfigInitializer extends ModuleInitializer {

    @CommandNode("reload")
    private int $reload(@CommandSource CommandContext<ServerCommandSource> ctx) {
        // reload configs
        Configs.configHandler.loadFromDisk();

        // reload modules
        Managers.getModuleManager().reloadModules();

        // reload jobs
        BaseJob.rescheduleAll();

        MessageHelper.sendMessage(ctx.getSource(), "reload");
        return CommandHelper.Return.SUCCESS;
    }

}
