package io.github.sakurawald.module.initializer.afk;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.afk.job.AfkMarkerJob;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;


public class AfkInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> updateJobs());
    }

    @Override
    public void onReload() {
        updateJobs();
    }

    public void updateJobs() {
        Managers.getScheduleManager().cancelJobs(AfkMarkerJob.class.getName());
        Managers.getScheduleManager().scheduleJob(AfkMarkerJob.class, Configs.configHandler.model().modules.afk.afk_checker.cron);
    }

    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("afk").executes(this::$afk));
    }

    private int $afk(@NotNull CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, (player -> {
            // note: issue command will update lastLastActionTime, so it's impossible to use /afk to disable afk
            ((AfkStateAccessor) player).fuji$setAfk(true);
            MessageHelper.sendMessage(player, "afk.on");
            return CommandHelper.Return.SUCCESS;
        }));
    }

}
