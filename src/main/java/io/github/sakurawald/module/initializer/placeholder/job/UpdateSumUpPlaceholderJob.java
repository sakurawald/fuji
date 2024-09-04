package io.github.sakurawald.module.initializer.placeholder.job;

import io.github.sakurawald.module.common.job.abst.CronJob;
import io.github.sakurawald.module.common.manager.impl.scheduler.ScheduleManager;
import io.github.sakurawald.module.initializer.placeholder.structure.SumUpPlaceholder;
import io.github.sakurawald.auxiliary.minecraft.ServerHelper;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;

public class UpdateSumUpPlaceholderJob extends CronJob {

    public UpdateSumUpPlaceholderJob() {
        super(() -> ScheduleManager.CRON_EVERY_MINUTE);
    }

    @Override
    public void execute(@NotNull JobExecutionContext context) {
        // save all online-player's stats into /stats/ folder
        MinecraftServer server = ServerHelper.getDefaultServer();
        server.getPlayerManager().getPlayerList().forEach((p) -> p.getStatHandler().save());

        // update
        SumUpPlaceholder.ofServer();
    }
}
