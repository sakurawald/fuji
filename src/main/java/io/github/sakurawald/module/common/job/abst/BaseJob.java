package io.github.sakurawald.module.common.job.abst;

import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.auxiliary.LogUtil;
import org.quartz.*;

import java.util.*;

public abstract class BaseJob implements Job {
    private static final Set<BaseJob> reschedulableJobs = new HashSet<>();

    protected boolean reschedulable = true;
    protected String jobGroup = null;
    protected String jobName = null;
    protected JobDetail jobDetail = null;
    protected TriggerKey triggerKey = null;

    // note: the no arguments constructor is only used for quartz to create and use the `execute` closure
    @SuppressWarnings("unused")
    public BaseJob() {
    }

    public BaseJob(String jobGroup, String jobName, JobDataMap jobDataMap) {
        if (jobGroup == null) {
            jobGroup = this.getClass().getName();
        }
        if (jobName == null) {
            jobName = UUID.randomUUID().toString();
        }

        // note: since quartz will construct AbstractJob using NoArgsConstructor, and all the AbstractJob instances used for `execute closure` have no jobDataMap.
        if (jobDataMap == null) {
            jobDataMap = new JobDataMap();
        }

        this.jobGroup = jobGroup;
        this.jobName = jobName;

        this.jobDetail = JobBuilder.newJob(this.getClass()).withIdentity(jobName, jobGroup).usingJobData(jobDataMap).build();
        this.triggerKey = new TriggerKey(jobName, jobGroup);
    }

    public abstract Trigger makeTrigger();

    public void schedule() {
        try {
            LogUtil.debug("Schedule job -> {}", this);
            Managers.getScheduleManager().scheduleJob(this.jobDetail, this.makeTrigger());

            if (this.reschedulable) {
                reschedulableJobs.add(this);
            }
        } catch (SchedulerException e) {
            LogUtil.error("Failed to schedule job: exception = {}, job = {}", e, this);
        }
    }

    public void reschedule() {
        try {
            LogUtil.debug("Re-schedule job -> {}", this);
            Managers.getScheduleManager().rescheduleJob(this.triggerKey, this.makeTrigger());
        } catch (SchedulerException e) {
            LogUtil.error("Failed to reschedule job: exception = {}, job = {}", e, this);
        }
    }

    public static void rescheduleAll() {
        for (BaseJob reschedulableJob : reschedulableJobs) {
            reschedulableJob.reschedule();
        }
    }

    @Override
    public String toString() {
        return "{jobGroup = %s, jobName = %s}".formatted(jobGroup, jobName);
    }
}
