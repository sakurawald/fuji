package io.github.sakurawald.module.initializer.cleaner.job;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.job.interfaces.CronJob;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.cleaner.CleanerInitializer;
import lombok.NoArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.function.Supplier;

public class CleanerJob extends CronJob {

    public static final CleanerInitializer INITIALIZER = Managers.getModuleManager().getInitializer(CleanerInitializer.class);

    public CleanerJob() {
        super(() -> Configs.configHandler.model().modules.cleaner.cron);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        INITIALIZER.clean();
    }
}
