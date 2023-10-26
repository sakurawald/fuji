package io.github.sakurawald.module.afk;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.ServerMain;
import io.github.sakurawald.config.base.ConfigManager;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.util.MessageUtil;
import io.github.sakurawald.util.ScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class AfkModule extends AbstractModule {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> updateJobs());
    }

    @Override
    public void onReload() {
        updateJobs();
    }

    public void updateJobs() {
        ScheduleUtil.removeJobs(AfkCheckerJob.class);
        ScheduleUtil.addJob(AfkCheckerJob.class, ConfigManager.configWrapper.instance().modules.afk.afk_checker.cron, new JobDataMap());
    }

    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("afk").executes(this::$afk));
    }

    @SuppressWarnings("SameReturnValue")
    private int $afk(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        // note: issue command will update lastLastActionTime, so it's impossible to use /afk to disable afk
        ((ServerPlayerAccessor_afk) player).sakurawald$setAfk(true);
        MessageUtil.sendMessage(player, "afk.on");
        return Command.SINGLE_SUCCESS;
    }

    public static class AfkCheckerJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            for (ServerPlayer player : ServerMain.SERVER.getPlayerList().getPlayers()) {
                ServerPlayerAccessor_afk afk_player = (ServerPlayerAccessor_afk) player;

                // get last action time
                long lastActionTime = player.getLastActionTime();
                long lastLastActionTime = afk_player.sakurawald$getLastLastActionTime();
                afk_player.sakurawald$setLastLastActionTime(lastActionTime);

                // diff last action time
                /* note:
                when a player joins the server,
                we'll set lastLastActionTime's initial value to Player#getLastActionTime(),
                but there are a little difference even if you call Player#getLastActionTime() again
                 */
                if (lastActionTime - lastLastActionTime <= 3000) {
                    if (afk_player.sakurawald$isAfk()) continue;

                    afk_player.sakurawald$setAfk(true);
                    MessageUtil.sendBroadcast("afk.on.broadcast", player.getGameProfile().getName());
                    if (ConfigManager.configWrapper.instance().modules.afk.afk_checker.kick_player) {
                        player.connection.disconnect(MessageUtil.ofVomponent(player, "afk.kick"));
                    }
                }
            }
        }
    }
}
