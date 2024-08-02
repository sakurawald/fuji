package io.github.sakurawald.module.initializer.command_scheduler;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.config.handler.interfaces.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.module.initializer.command_scheduler.model.SchedulerModel;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.common.manager.scheduler.ScheduleManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_scheduler.adapter.ScheduleJobName;
import io.github.sakurawald.module.initializer.command_scheduler.job.CommandScheduleJob;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.CommandHelper;
import lombok.Getter;
import org.quartz.JobDataMap;


@Command("scheduler")
@CommandPermission(level = 4)
public class CommandSchedulerInitializer extends ModuleInitializer {

    @Getter
    private static final ConfigHandler<SchedulerModel> schedulerHandler = new ObjectConfigHandler<>("scheduler.json", SchedulerModel.class);

    private void updateJobs() {
        Managers.getScheduleManager().deleteJobs(CommandScheduleJob.class);
        schedulerHandler.model().scheduleJobs.forEach(scheduleJob -> {
            if (scheduleJob.enable) {
                scheduleJob.crons.forEach(cron -> {
                    new CommandScheduleJob(new JobDataMap() {
                        {
                            this.put("job", scheduleJob);
                        }
                    }, () -> cron).schedule();
                });

                LogUtil.info("[Command Scheduler] schedule job -> {}", scheduleJob.getName());
            }
        });
    }

    @Override
    public void onInitialize() {
        schedulerHandler.loadFromDisk();
        schedulerHandler.setAutoSaveJob(ScheduleManager.CRON_EVERY_MINUTE);
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

}

