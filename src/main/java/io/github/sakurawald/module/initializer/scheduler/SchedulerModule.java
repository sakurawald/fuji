package io.github.sakurawald.module.initializer.scheduler;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.ScheduleUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;


public class SchedulerModule extends ModuleInitializer {

    private void updateJobs() {
        ScheduleUtil.removeJobs(ScheduleJobJob.class.getName());
        Configs.schedulerHandler.model().scheduleJobs.forEach(scheduleJob -> {

            if (scheduleJob.enable) {
                scheduleJob.crons.forEach(cron -> ScheduleUtil.addJob(ScheduleJobJob.class, null, null, cron, new JobDataMap() {
                    {
                        this.put("job", scheduleJob);
                    }
                }));
                Fuji.LOGGER.info("SchedulerModule: Add ScheduleJob {}", scheduleJob);
            }
        });
    }

    @Override
    public void onInitialize() {
        Configs.schedulerHandler.loadFromDisk();
        updateJobs();
    }

    @Override
    public void onReload() {
        Configs.schedulerHandler.loadFromDisk();
        updateJobs();
    }

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("scheduler_trigger").requires(s -> s.hasPermissionLevel(4))
                .then(argument("name", StringArgumentType.word()).suggests(new SchedulerJobSuggestionProvider()).executes(this::$scheduler_trigger)));
    }

    private int $scheduler_trigger(CommandContext<ServerCommandSource> ctx) {
        String name = StringArgumentType.getString(ctx, "name");

        Configs.schedulerHandler.model().scheduleJobs.forEach(job -> {
            if (job.name.equals(name)) {
                job.trigger();
            }
        });
        return Command.SINGLE_SUCCESS;
    }


    private static class SchedulerJobSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext context, SuggestionsBuilder builder) {
            Configs.schedulerHandler.model().scheduleJobs.forEach(job -> builder.suggest(job.name));
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

