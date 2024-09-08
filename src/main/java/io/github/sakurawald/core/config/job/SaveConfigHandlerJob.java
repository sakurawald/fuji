package io.github.sakurawald.core.config.job;

import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.config.handler.abst.ConfigHandler;
import io.github.sakurawald.core.job.abst.CronJob;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.function.Supplier;

@NoArgsConstructor
public class SaveConfigHandlerJob extends CronJob {

    public SaveConfigHandlerJob(String jobName, JobDataMap jobDataMap, Supplier<String> cronSupplier) {
        super(null, jobName, jobDataMap, cronSupplier);
    }

    @Override
    public void execute(@NotNull JobExecutionContext context) {
        LogUtil.debug("AutoSave ConfigWrapper {}", context.getJobDetail().getKey().getName());
        ConfigHandler<?> configHandler = (ConfigHandler<?>) context.getJobDetail().getJobDataMap().get(ConfigHandler.class.getName());
        configHandler.saveToDisk();
    }
}
