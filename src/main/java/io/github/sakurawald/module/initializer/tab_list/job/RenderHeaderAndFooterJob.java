package io.github.sakurawald.module.initializer.tab_list.job;

import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.job.abst.CronJob;
import io.github.sakurawald.module.initializer.tab_list.TabListInitializer;
import org.quartz.JobExecutionContext;

public class RenderHeaderAndFooterJob extends CronJob {

    public RenderHeaderAndFooterJob() {
        super(() -> Configs.configHandler.getModel().modules.tab_list.update_cron);
    }

    @Override
    public void execute(JobExecutionContext context) {
        TabListInitializer.render(ServerHelper.getDefaultServer());
    }
}
