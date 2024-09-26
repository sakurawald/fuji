package io.github.sakurawald.module.initializer.tab_list.job;

import io.github.sakurawald.core.job.abst.CronJob;
import io.github.sakurawald.module.initializer.tab_list.TabListInitializer;
import org.quartz.JobExecutionContext;

public class RenderHeaderAndFooterJob extends CronJob {

    public RenderHeaderAndFooterJob() {
        super(() -> TabListInitializer.config.getModel().update_cron);
    }

    @Override
    public void execute(JobExecutionContext context) {
        TabListInitializer.render();
    }
}
