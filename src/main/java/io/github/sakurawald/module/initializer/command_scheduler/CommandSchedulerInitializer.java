package io.github.sakurawald.module.initializer.command_scheduler;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.config.transformer.impl.MoveFileIntoModuleConfigDirectoryTransformer;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.manager.impl.scheduler.ScheduleManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_scheduler.command.argument.wrapper.ScheduleJobName;
import io.github.sakurawald.module.initializer.command_scheduler.config.model.CommandSchedulerConfigModel;
import io.github.sakurawald.module.initializer.command_scheduler.job.CommandScheduleJob;
import lombok.Getter;
import org.quartz.JobDataMap;


@CommandNode("command-scheduler")
@CommandRequirement(level = 4)
public class CommandSchedulerInitializer extends ModuleInitializer {

    @Getter
    private static final BaseConfigurationHandler<CommandSchedulerConfigModel> schedulerHandler = new ObjectConfigurationHandler<>("scheduler.json", CommandSchedulerConfigModel.class)
        .addTransformer(new MoveFileIntoModuleConfigDirectoryTransformer(Fuji.CONFIG_PATH.resolve("scheduler.json"),CommandSchedulerInitializer.class));

    private void updateJobs() {
        Managers.getScheduleManager().deleteJobs(CommandScheduleJob.class);
        schedulerHandler.model().jobs.forEach(scheduleJob -> {
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
        schedulerHandler.scheduleSaveConfigurationHandlerJob(ScheduleManager.CRON_EVERY_MINUTE);
        updateJobs();
    }

    @Override
    public void onReload() {
        updateJobs();
    }

    @CommandNode("trigger")
    private static int $trigger(ScheduleJobName jobName) {
        schedulerHandler.model().jobs.forEach(job -> {
            if (job.getName().equals(jobName.getValue())) {
                job.trigger();
            }
        });

        return CommandHelper.Return.SUCCESS;
    }

}

