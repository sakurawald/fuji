package io.github.sakurawald.module.scheduler;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.util.ScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;

@Slf4j
public class SchedulerModule extends AbstractModule {

    private void updateJobs() {
        ScheduleUtil.removeJobs(ScheduleJobJob.class);

        ConfigManager.schedulerWrapper.instance().scheduleJobs.forEach(scheduleJob -> {

            if (scheduleJob.enable) {
                scheduleJob.crons.forEach(cron -> ScheduleUtil.addJob(ScheduleJobJob.class, cron, new JobDataMap() {
                    {
                        this.put("job", scheduleJob);
                    }
                }));
                log.info("SchedulerModule: Add ScheduleJob {}", scheduleJob);
            }
        });
    }

    @Override
    public void onInitialize() {
        ConfigManager.schedulerWrapper.loadFromDisk();
        updateJobs();

        CommandRegistrationCallback.EVENT.register(this::registerCommand);
    }

    @Override
    public void onReload() {
        ConfigManager.schedulerWrapper.loadFromDisk();
        updateJobs();
    }

    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("scheduler_trigger").requires(s -> s.hasPermission(4))
                .then(argument("name", StringArgumentType.word()).suggests(new SchedulerJobSuggestionProvider()).executes(this::$scheduler_trigger)));
    }

    private int $scheduler_trigger(CommandContext<CommandSourceStack> ctx) {
        String name = StringArgumentType.getString(ctx, "name");

        ConfigManager.schedulerWrapper.instance().scheduleJobs.forEach(job -> {
            if (job.name.equals(name)) {
                job.trigger();
            }
        });
        return Command.SINGLE_SUCCESS;
    }


    private static class SchedulerJobSuggestionProvider implements SuggestionProvider<CommandSourceStack> {

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext context, SuggestionsBuilder builder) {
            ConfigManager.schedulerWrapper.instance().scheduleJobs.forEach(job -> builder.suggest(job.name));
            return builder.buildFuture();
        }
    }

    public static class ScheduleJobJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            ScheduleJob job = (ScheduleJob) context.getJobDetail().getJobDataMap().get("job");
            job.trigger();
        }
    }
}

