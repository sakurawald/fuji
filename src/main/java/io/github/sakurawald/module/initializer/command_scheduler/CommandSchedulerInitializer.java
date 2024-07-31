package io.github.sakurawald.module.initializer.command_scheduler;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.SchedulerModel;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_scheduler.adapter.ScheduleJobName;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.CommandHelper;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;


@Command("scheduler")
@CommandPermission(level = 4)
public class CommandSchedulerInitializer extends ModuleInitializer {

    @Getter
    private static final ConfigHandler<SchedulerModel> schedulerHandler = new ObjectConfigHandler<>("scheduler.json", SchedulerModel.class);

    private void updateJobs() {
        Managers.getScheduleManager().cancelJobs(ScheduleJobJob.class.getName());
        schedulerHandler.model().scheduleJobs.forEach(scheduleJob -> {

            if (scheduleJob.enable) {
                scheduleJob.crons.forEach(cron -> Managers.getScheduleManager().scheduleJob(ScheduleJobJob.class, cron, new JobDataMap() {
                    {
                        this.put("job", scheduleJob);
                    }
                }));
                LogUtil.info("SchedulerModule: Add ScheduleJob {}", scheduleJob);
            }
        });
    }

    @Override
    public void onInitialize() {
        schedulerHandler.loadFromDisk();
        updateJobs();
    }

    @Override
    public void onReload() {
        schedulerHandler.loadFromDisk();
        updateJobs();
    }

    @Command("trigger")
    private int $trigger(ScheduleJobName jobName) {
        schedulerHandler.model().scheduleJobs.forEach(job -> {
            if (job.name.equals(jobName.getName())) {
                job.trigger();
            }
        });

        return CommandHelper.Return.SUCCESS;
    }

    public static class ScheduleJobJob implements Job {

        @Override
        public void execute(@NotNull JobExecutionContext context) {
            ScheduleJob job = (ScheduleJob) context.getJobDetail().getJobDataMap().get("job");
            job.trigger();
        }
    }
}

