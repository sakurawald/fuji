package io.github.sakurawald.module.initializer.command_scheduler;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.SchedulerModel;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.literal;


public class CommandSchedulerInitializer extends ModuleInitializer {

    public static final ConfigHandler<SchedulerModel> schedulerHandler = new ObjectConfigHandler<>("scheduler.json", SchedulerModel.class);

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

    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("scheduler").requires(s -> s.hasPermissionLevel(4))
                .then(literal("trigger")
                        .then(CommandHelper.Argument.name().suggests(new SchedulerJobSuggestionProvider()).executes(this::$trigger))));
    }

    private int $trigger(@NotNull CommandContext<ServerCommandSource> ctx) {
        String name = CommandHelper.Argument.name(ctx);

        schedulerHandler.model().scheduleJobs.forEach(job -> {
            if (job.name.equals(name)) {
                job.trigger();
            }
        });

        return CommandHelper.Return.SUCCESS;
    }


    private static class SchedulerJobSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext context, @NotNull SuggestionsBuilder builder) {
            schedulerHandler.model().scheduleJobs.forEach(job -> builder.suggest(job.name));
            return builder.buildFuture();
        }
    }

    public static class ScheduleJobJob implements Job {

        @Override
        public void execute(@NotNull JobExecutionContext context) {
            ScheduleJob job = (ScheduleJob) context.getJobDetail().getJobDataMap().get("job");
            job.trigger();
        }
    }
}

