package io.github.sakurawald.module.initializer.command_scheduler.job;

import io.github.sakurawald.module.common.job.interfaces.CronJob;
import io.github.sakurawald.module.initializer.command_scheduler.ScheduleJob;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.function.Supplier;

@NoArgsConstructor
public class CommandScheduleJob extends CronJob {

    public CommandScheduleJob(JobDataMap jobDataMap, Supplier<String> cronSupplier) {
        super(jobDataMap, cronSupplier);
    }

    @Override
    public void execute(@NotNull JobExecutionContext context) {
        ScheduleJob job = (ScheduleJob) super.jobDataMap.get("job");
        job.trigger();
    }
}
