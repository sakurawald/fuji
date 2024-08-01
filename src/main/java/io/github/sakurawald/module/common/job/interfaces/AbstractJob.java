package io.github.sakurawald.module.common.job.interfaces;

import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.util.LogUtil;
import org.quartz.*;

import java.util.*;

public abstract class AbstractJob implements Job {
    private static final Set<AbstractJob> reschedulableJobs = new HashSet<>();

    protected boolean reschedulable = true;
    protected String jobGroup = null;
    protected String jobName = null;
    protected JobDataMap jobDataMap = null;
    protected JobDetail jobDetail = null;
    protected TriggerKey triggerKey = null;

    // note: the no arguments constructor is only used for quartz to create and use the `execute` closure
    @SuppressWarnings("unused")
    public AbstractJob() {
    }

    public AbstractJob(String jobGroup, String jobName, JobDataMap jobDataMap) {
        if (jobGroup == null) {
            jobGroup = this.getClass().getName();
        }
        if (jobName == null) {
            jobName = UUID.randomUUID().toString();
        }
        if (jobDataMap == null) {
            jobDataMap = new JobDataMap();
        }

        this.jobGroup = jobGroup;
        this.jobName = jobName;
        this.jobDataMap = jobDataMap;

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
        for (AbstractJob reschedulableJob : reschedulableJobs) {
            reschedulableJob.reschedule();
        }
    }

    @Override
    public String toString() {
        return "{jobGroup = %s, jobName = %s}".formatted(jobGroup, jobName);
    }
}
