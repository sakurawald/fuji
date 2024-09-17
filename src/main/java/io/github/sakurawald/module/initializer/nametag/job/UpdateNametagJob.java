package io.github.sakurawald.module.initializer.nametag.job;

import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.job.abst.CronJob;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.nametag.NametagInitializer;
import org.quartz.JobExecutionContext;

public class UpdateNametagJob extends CronJob {

    private final NametagInitializer nametagInitializer = Managers.getModuleManager().getInitializer(NametagInitializer.class);

    public UpdateNametagJob() {
        super(() -> Configs.configHandler.getModel().modules.nametag.update_cron);
    }

    @Override
    public void execute(JobExecutionContext context) {
        nametagInitializer.updateNametags();
    }
}
