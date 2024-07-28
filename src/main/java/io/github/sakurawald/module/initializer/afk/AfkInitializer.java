package io.github.sakurawald.module.initializer.afk;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.job.NPassMarkerJob;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Collection;
import java.util.List;


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
        Managers.getScheduleManager().cancelJobs(AfkCheckerJob.class.getName());
        Managers.getScheduleManager().scheduleJob(AfkCheckerJob.class, Configs.configHandler.model().modules.afk.afk_checker.cron);
    }

    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("afk").executes(this::$afk));
    }

    @SuppressWarnings("SameReturnValue")
    private int $afk(@NotNull CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, (player -> {
            // note: issue command will update lastLastActionTime, so it's impossible to use /afk to disable afk
            ((AfkStateAccessor) player).fuji$setAfk(true);
            MessageHelper.sendMessage(player, "afk.on");
            return CommandHelper.Return.SUCCESS;
        }));
    }

    public static class AfkCheckerJob extends NPassMarkerJob<ServerPlayerEntity> {

        public AfkCheckerJob() {
            super(1, Configs.configHandler.model().modules.afk.afk_checker.cron);
        }

        @Override
        public Collection<ServerPlayerEntity> getEntityList() {
            return ServerHelper.getDefaultServer().getPlayerManager().getPlayerList();
        }

        @Override
        public boolean shouldMark(ServerPlayerEntity entity) {
            if (entity.isRemoved()) return false;

            AfkStateAccessor afk_player = (AfkStateAccessor) entity;

            // get last action time
            long lastActionTime = entity.getLastActionTime();
            long lastLastActionTime = afk_player.fuji$getLastLastActionTime();
            afk_player.fuji$setLastLastActionTime(lastActionTime);

            // diff last action time
                /* note:
                when a player joins the server,
                we'll set lastLastActionTime's initial value to Player#getLastActionTime(),
                but there are a little difference even if you call Player#getLastActionTime() again
                 */
            if (lastActionTime - lastLastActionTime <= 3000) {
                if (afk_player.fuji$isAfk()) return false;
            }

            return true;
        }

        @Override
        public void onCompleted(ServerPlayerEntity entity) {
            AfkStateAccessor afk_player = (AfkStateAccessor) entity;
            afk_player.fuji$setAfk(true);
            if (Configs.configHandler.model().modules.afk.afk_checker.kick_player) {
                entity.networkHandler.disconnect(MessageHelper.ofText(entity, "afk.kick"));
            }
        }
    }
}
