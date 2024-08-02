package io.github.sakurawald.config.job;

import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.module.common.job.interfaces.CronJob;
import io.github.sakurawald.util.LogUtil;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.function.Supplier;

@NoArgsConstructor
public class ConfigHandlerAutoSaveJob extends CronJob {

    public ConfigHandlerAutoSaveJob(String jobName, JobDataMap jobDataMap, Supplier<String> cronSupplier) {
        super(null, jobName, jobDataMap, cronSupplier);
    }

    @Override
    public void execute(@NotNull JobExecutionContext context) throws JobExecutionException {
        LogUtil.debug("AutoSave ConfigWrapper {}", context.getJobDetail().getKey().getName());
        ConfigHandler<?> configHandler = (ConfigHandler<?>) context.getJobDetail().getJobDataMap().get(ConfigHandler.class.getName());
        configHandler.saveToDisk();
    }
}
