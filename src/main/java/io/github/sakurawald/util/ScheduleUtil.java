package io.github.sakurawald.util;


import io.github.sakurawald.config.Configs;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;


@UtilityClass
public class ScheduleUtil {

    public static final String CRON_EVERY_MINUTE = "0 * * ? * * *";

    @Getter
    private static Scheduler scheduler;

    static {
        /* set logger level for quartz */
        Level level = Level.getLevel(Configs.configHandler.model().common.quartz.logger_level);
        Configurator.setAllLevels("org.quartz", level);

        // note: for some early initialize, here will cause NPE
        resetScheduler();
    }

    public static void addJob(@NotNull Class<? extends Job> jobClass, @Nullable String jobName, @Nullable String jobGroup, @NotNull String cron, @Nullable JobDataMap jobDataMap) {
        if (jobName == null) {
            jobName = UUID.randomUUID().toString();
        }
        if (jobGroup == null) {
            jobGroup = jobClass.getName();
        }
        if (jobDataMap == null) {
            jobDataMap = new JobDataMap();
        }
        LogUtil.debug("addJob() -> jobClass: {}, jobName: {}, jobGroup: {}, cron: {}, jobDataMap: {}", jobClass, jobName, jobGroup, cron, jobDataMap);

        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).usingJobData(jobDataMap).build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            LogUtil.error("Exception in ScheduleUtil.addJob", e);
        }
    }

    public static void addJob(@NotNull Class<? extends Job> jobClass, @Nullable String jobName, @Nullable String jobGroup, int intervalMs, int repeatCount, @Nullable JobDataMap jobDataMap) {
        if (jobName == null) {
            jobName = UUID.randomUUID().toString();
        }
        if (jobGroup == null) {
            jobGroup = jobClass.getName();
        }
        if (jobDataMap == null) {
            jobDataMap = new JobDataMap();
        }
        LogUtil.debug("addJob() -> jobClass: {}, jobName: {}, jobGroup: {}, intervalMs: {}, repeatCount: {}, jobDataMap: {}", jobClass, jobName, jobGroup, intervalMs, repeatCount, jobDataMap);

        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).usingJobData(jobDataMap).build();
        SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(intervalMs).withRepeatCount(repeatCount - 1)).build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            LogUtil.error("Exception in ScheduleUtil.addJob", e);
        }
    }

    public static void removeJobs(String jobGroup, @NotNull String jobName) {
        LogUtil.debug("removeJobs() -> jobGroup: {}, jobName: {}", jobGroup, jobName);

        try {
            scheduler.deleteJob(new JobKey(jobName, jobGroup));
        } catch (SchedulerException e) {
            LogUtil.error("Exception in ScheduleUtil.removeJobs", e);
        }
    }

    public static void removeJobs(@NotNull String jobGroup) {
        LogUtil.debug("removeJobs() -> jobGroup: {}", jobGroup);

        try {
            scheduler.deleteJobs(getJobKeys(jobGroup).stream().toList());
        } catch (SchedulerException e) {
            LogUtil.error("Exception in ScheduleUtil.removeJobs", e);
        }
    }

    private static Set<JobKey> getJobKeys(@NotNull String jobGroup) {
        GroupMatcher<JobKey> groupMatcher = GroupMatcher.groupEquals(jobGroup);
        try {
            return scheduler.getJobKeys(groupMatcher);
        } catch (SchedulerException e) {
            LogUtil.error("Exception in ScheduleUtil.getJobKeys", e);
        }
        return Collections.emptySet();
    }

    public static void triggerJobs(@NotNull String jobGroup) {
        getJobKeys(jobGroup).forEach(jobKey -> {
            try {
                scheduler.triggerJob(jobKey);
            } catch (SchedulerException e) {
                LogUtil.error("Exception in ScheduleUtil.triggerJobs", e);
            }
        });
    }

    private static void resetScheduler() {
        /* new scheduler */
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public static void startScheduler() {
        resetScheduler();

        try {
            scheduler.start();
        } catch (SchedulerException e) {
            LogUtil.error("Exception in ScheduleUtil.startScheduler", e);
        }
    }

    public static void shutdownScheduler() {
        try {
            scheduler.shutdown();

            // note: reset scheduler right now to fix client-side NPE
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                resetScheduler();
            }

        } catch (SchedulerException e) {
            LogUtil.error("Exception in ScheduleUtil.shutdownScheduler", e);
        }
    }
}
