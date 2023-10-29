package io.github.sakurawald.util;


import io.github.sakurawald.config.base.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

@Slf4j
public class ScheduleUtil {
    public static final String CRON_EVERY_MINUTE = "0 * * ? * * *";
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
        addJob(jobClass, jobClass.getName(), cron, jobDataMap);
    }

    public static void addJob(Class<? extends Job> jobClass, String jobName, String cron, JobDataMap jobDataMap) {
        log.debug("addJob() -> jobClass: {}, jobName: {}, cron: {}, jobDataMap: {}", jobClass, jobName, cron, jobDataMap);
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName).usingJobData(jobDataMap).build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Exception in ScheduleUtil.addJob", e);
        }
    }

    public static void removeJobs(Class<?> clazz) {
        removeJobs(clazz.getName());
    }

    public static void removeJobs(String jobName) {
        try {
            boolean b = scheduler.deleteJob(new JobKey(jobName));
            log.debug("removeJobs() -> jobName: {}, result: {}", jobName, b);
        } catch (SchedulerException e) {
            log.error("Exception in ScheduleUtil.removeJobs", e);
        }
    }

    public static void addJob(Class<? extends Job> jobClass, String jobName, int intervalMs, int repeatCount, JobDataMap jobDataMap) {
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName).usingJobData(jobDataMap).build();
        SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName).withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(intervalMs).withRepeatCount(repeatCount - 1)).build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Exception in ScheduleUtil.addJob", e);
        }
    }

    public static void addJob(Class<? extends Job> jobClass, int intervalMs, int repeatCount, JobDataMap jobDataMap) {
        addJob(jobClass, jobClass.getName(), intervalMs, repeatCount, jobDataMap);
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
