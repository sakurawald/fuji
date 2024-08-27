package io.github.sakurawald.module.initializer.nametag.job;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.job.interfaces.CronJob;
import io.github.sakurawald.module.initializer.nametag.NametagInitializer;
import io.github.sakurawald.auxiliary.minecraft.ServerHelper;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class UpdateNametagJob extends CronJob {

    public UpdateNametagJob() {
        super(() -> Configs.configHandler.model().modules.nametag.update_cron);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        NametagInitializer.update();
    }
}
