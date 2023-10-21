package io.github.sakurawald.module.scheduler;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.ServerMain;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.util.ScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.MinecraftServer;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class SchedulerModule extends AbstractModule {

    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.configWrapper.instance().modules.scheduler.enable;
    }

    private void updateJobs() {
        ScheduleUtil.removeJobs(ScheduleJob.class.getName());

        ConfigManager.schedulerWrapper.instance().scheduleJobs.forEach(scheduleJob -> {

            if (scheduleJob.enable) {
                ScheduleUtil.addJob(ScheduleJob.class, scheduleJob.cron, new JobDataMap() {
                    {
                        this.put("name", scheduleJob.name);
                        this.put("commands", scheduleJob.commands);
                    }
                });
                log.info("SchedulerModule: Add ScheduleJob {}", scheduleJob);
            }
        });
    }

    @Override
    public void onInitialize() {
        ConfigManager.schedulerWrapper.loadFromDisk();
        updateJobs();
    }

    @Override
    public void onReload() {
        ConfigManager.schedulerWrapper.loadFromDisk();
        updateJobs();
    }


    public static class ScheduleJob implements Job {

        @SuppressWarnings("unchecked")
        @Override
        public void execute(JobExecutionContext context) {

            String name = (String) context.getJobDetail().getJobDataMap().get("name");
            List<String> commands = (List<String>) context.getJobDetail().getJobDataMap().get("commands");

            log.info("SchedulerModule: Execute ScheduleJob [name = " + name + ", commands = " + commands + "].");

            for (String command : commands) {

                MinecraftServer server = ServerMain.SERVER;
                try {
                    server.getCommands().getDispatcher().execute(command, server.createCommandSourceStack());
                } catch (CommandSyntaxException e) {
                    log.error("SchedulerModule: Execute ScheduleJob [name = " + name + ", commands = " + commands + "] failed.", e);
                }
            }
        }
    }
}

