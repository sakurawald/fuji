package io.github.sakurawald.module.initializer.placeholder.job;

import io.github.sakurawald.module.common.job.interfaces.CronJob;
import io.github.sakurawald.module.common.manager.scheduler.ScheduleManager;
import io.github.sakurawald.module.initializer.placeholder.structure.SumUpPlaceholder;
import io.github.sakurawald.util.minecraft.ServerHelper;
import lombok.NoArgsConstructor;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.function.Supplier;

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
