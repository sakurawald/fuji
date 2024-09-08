package io.github.sakurawald.module.initializer.config;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LanguageHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.job.abst.BaseJob;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
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

        LanguageHelper.sendMessageByKey(ctx.getSource(), "reload");
        return CommandHelper.Return.SUCCESS;
    }

}
