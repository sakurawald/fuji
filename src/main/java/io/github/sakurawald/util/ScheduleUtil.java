package io.github.sakurawald.util;


import io.github.sakurawald.config.base.ConfigManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.Set;
import java.util.UUID;

@Slf4j
public class ScheduleUtil {
    public static final String CRON_EVERY_MINUTE = "0 * * ? * * *";
    @Getter
    private static final Scheduler scheduler;

    static {
        /* set logger level for quartz */
        Level level = Level.getLevel(ConfigManager.configWrapper.instance().common.quartz.logger_level);
        Configurator.setAllLevels("org.quartz", level);

        /* new scheduler */
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addJob(Class<? extends Job> jobClass, String jobName, String jobGroup, String cron, JobDataMap jobDataMap) {
        if (jobName == null) {
            jobName = UUID.randomUUID().toString();
        }
        if (jobGroup == null) {
            jobGroup = jobClass.getName();
        }
        log.debug("addJob() -> jobClass: {}, jobName: {}, jobGroup: {}, cron: {}, jobDataMap: {}", jobClass, jobName, jobGroup, cron, jobDataMap);
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).usingJobData(jobDataMap).build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Exception in ScheduleUtil.addJob", e);
        }
    }

    public static void addJob(Class<? extends Job> jobClass, String jobName, String jobGroup, int intervalMs, int repeatCount, JobDataMap jobDataMap) {
        if (jobName == null) {
            jobName = UUID.randomUUID().toString();
        }
        if (jobGroup == null) {
            jobGroup = jobClass.getName();
        }
        log.debug("addJob() -> jobClass: {}, jobName: {}, jobGroup: {}, intervalMs: {}, repeatCount: {}, jobDataMap: {}", jobClass, jobName, jobGroup, intervalMs, repeatCount, jobDataMap);
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).usingJobData(jobDataMap).build();
        SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(intervalMs).withRepeatCount(repeatCount - 1)).build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Exception in ScheduleUtil.addJob", e);
        }
    }

    public static void removeJobs(String jobName, String jobGroup) {
        try {
            boolean b = scheduler.deleteJob(new JobKey(jobName, jobGroup));
            log.debug("removeJobs() -> jobName: {}, jobGroup: {}, result: {}", jobName, jobGroup, b);
        } catch (SchedulerException e) {
            log.error("Exception in ScheduleUtil.removeJobs", e);
        }
    }

    public static void removeJobs(String jobGroup) {
        try {
            GroupMatcher<JobKey> groupMatcher = GroupMatcher.groupEquals(jobGroup);
            Set<JobKey> jobKeys = scheduler.getJobKeys(groupMatcher);
            scheduler.deleteJobs(jobKeys.stream().toList());
            log.debug("removeJobs() -> jobGroup: {}", jobGroup);
        } catch (SchedulerException e) {
            log.error("Exception in ScheduleUtil.removeJobs", e);
        }
    }


    public static void startScheduler() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("Exception in ScheduleUtil.startScheduler", e);
        }
    }

    public static void shutdownScheduler() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            log.error("Exception in ScheduleUtil.shutdownScheduler", e);
        }
    }
}
