package io.github.sakurawald.module.initializer.command_scheduler.job;

import io.github.sakurawald.module.common.job.abst.CronJob;
import io.github.sakurawald.module.initializer.command_scheduler.structure.ScheduleJob;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.function.Supplier;

@NoArgsConstructor
public class CommandScheduleJob extends CronJob {

    public CommandScheduleJob(JobDataMap jobDataMap, Supplier<String> cronSupplier) {
        super(jobDataMap, cronSupplier);

        super.reschedulable = false;
    }

    @Override
    public void execute(@NotNull JobExecutionContext context) {
        ScheduleJob job = (ScheduleJob) context.getJobDetail().getJobDataMap().get("job");

        if (job.isEnable()) {
            job.trigger();
        }
    }
}
