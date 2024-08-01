package io.github.sakurawald.module.initializer.tab_list.job;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.job.interfaces.AbstractJob;
import io.github.sakurawald.module.common.job.interfaces.CronJob;
import io.github.sakurawald.module.initializer.tab_list.TabListInitializer;
import io.github.sakurawald.util.minecraft.ServerHelper;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RenderHeaderAndFooterJob extends CronJob {

    public RenderHeaderAndFooterJob() {
        super(() -> Configs.configHandler.model().modules.tab_list.update_cron);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        TabListInitializer.render(ServerHelper.getDefaultServer());
    }
}
