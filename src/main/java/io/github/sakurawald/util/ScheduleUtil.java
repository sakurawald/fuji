package io.github.sakurawald.util;


import io.github.sakurawald.config.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

@Slf4j
public class ScheduleUtil {
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

    public static void addJob(Class<? extends Job> jobClass, String cron, JobDataMap jobDataMap) {
        String name = jobClass.getName();

        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(name).usingJobData(jobDataMap).build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Exception in ScheduleUtil.addJob", e);
        }
    }

    public static void removeJobs(String name) {
        try {
            scheduler.deleteJob(new JobKey(name));
        } catch (SchedulerException e) {
            log.error("Exception in ScheduleUtil.removeJobs", e);
        }
    }

    public static void addJob(Class<? extends Job> jobClass, int intervalMs, int repeatCount, JobDataMap jobDataMap) {
        String name = jobClass.getName();

        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(name).usingJobData(jobDataMap).build();
        SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name).withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(intervalMs).withRepeatCount(repeatCount - 1)).build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Exception in ScheduleUtil.addJob", e);
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
