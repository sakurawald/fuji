package io.github.sakurawald.module.common.job.abst;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.TriggerBuilder;

import java.util.function.Supplier;

public abstract class CronJob extends BaseJob {

    Supplier<String> cronSupplier;

    @SuppressWarnings("unused")
    public CronJob() {}

    public CronJob(String jobGroup, String jobName, JobDataMap jobDataMap, Supplier<String> cronSupplier) {
        super(jobGroup, jobName, jobDataMap);
        this.cronSupplier = cronSupplier;
    }

    public CronJob(JobDataMap jobDataMap, Supplier<String> cronSupplier) {
        super(null, null, jobDataMap);
        this.cronSupplier = cronSupplier;
    }

    public CronJob(Supplier<String> cronSupplier) {
        this(null, cronSupplier);
    }

    @Override
    public CronTrigger makeTrigger() {
        return TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(CronScheduleBuilder.cronSchedule(this.cronSupplier.get())).build();
    }
}
