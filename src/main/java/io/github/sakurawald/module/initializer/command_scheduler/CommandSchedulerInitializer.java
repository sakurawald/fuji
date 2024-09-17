package io.github.sakurawald.module.initializer.command_scheduler;

import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.config.handler.abst.ConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.manager.impl.scheduler.ScheduleManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_scheduler.command.argument.wrapper.ScheduleJobName;
import io.github.sakurawald.module.initializer.command_scheduler.config.model.SchedulerModel;
import io.github.sakurawald.module.initializer.command_scheduler.job.CommandScheduleJob;
import lombok.Getter;
import org.quartz.JobDataMap;


@CommandNode("command-scheduler")
@CommandRequirement(level = 4)
public class CommandSchedulerInitializer extends ModuleInitializer {

    @Getter
    private static final ConfigurationHandler<SchedulerModel> schedulerHandler = new ObjectConfigurationHandler<>("scheduler.json", SchedulerModel.class);

    private void updateJobs() {
        Managers.getScheduleManager().deleteJobs(CommandScheduleJob.class);
        schedulerHandler.model().scheduleJobs.forEach(scheduleJob -> {
            if (scheduleJob.isEnable()) {
                scheduleJob.getCrons().forEach(cron -> new CommandScheduleJob(new JobDataMap() {
                    {
                        this.put("job", scheduleJob);
                    }
                }, () -> cron).schedule());

                LogUtil.info("[command scheduler] schedule job -> {}", scheduleJob.getName());
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

    @CommandNode("trigger")
    private int $trigger(ScheduleJobName jobName) {
        schedulerHandler.model().scheduleJobs.forEach(job -> {
            if (job.getName().equals(jobName.getValue())) {
                job.trigger();
            }
        });

        return CommandHelper.Return.SUCCESS;
    }

}

