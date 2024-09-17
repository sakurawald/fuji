package io.github.sakurawald.core.config.job;

import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.config.handler.abst.ConfigurationHandler;
import io.github.sakurawald.core.job.abst.CronJob;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.function.Supplier;

@NoArgsConstructor
public class SaveConfigurationHandlerJob extends CronJob {

    public SaveConfigurationHandlerJob(String jobName, JobDataMap jobDataMap, Supplier<String> cronSupplier) {
        super(null, jobName, jobDataMap, cronSupplier);
    }

    @Override
    public void execute(@NotNull JobExecutionContext context) {
        LogUtil.debug("save configuration file {}", context.getJobDetail().getKey().getName());

        ConfigurationHandler<?> configHandler = (ConfigurationHandler<?>) context.getJobDetail().getJobDataMap().get(ConfigurationHandler.class.getName());
        configHandler.writeDisk();
    }
}
