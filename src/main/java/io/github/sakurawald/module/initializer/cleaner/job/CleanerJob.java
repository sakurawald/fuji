package io.github.sakurawald.module.initializer.cleaner.job;

import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.job.abst.CronJob;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.cleaner.CleanerInitializer;
import org.quartz.JobExecutionContext;

public class CleanerJob extends CronJob {

    public static final CleanerInitializer INITIALIZER = Managers.getModuleManager().getInitializer(CleanerInitializer.class);

    public CleanerJob() {
        super(() -> INITIALIZER.storage.getModel().cron);
    }

    @Override
    public void execute(JobExecutionContext context) {
        INITIALIZER.clean();
    }
}
