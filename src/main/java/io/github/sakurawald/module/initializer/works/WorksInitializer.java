package io.github.sakurawald.module.initializer.works;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.WorksModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.works.gui.WorksGui;
import io.github.sakurawald.module.initializer.works.work_type.Work;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.ScheduleUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("SameReturnValue")

public class WorksInitializer extends ModuleInitializer {

    public static final ConfigHandler<WorksModel> worksHandler = new ObjectConfigHandler<>("works.json", WorksModel.class);


    @Override
    public void onInitialize() {
        worksHandler.loadFromDisk();
        ServerLifecycleEvents.SERVER_STARTED.register(this::registerScheduleTask);
    }

    @Override
    public void onReload() {
        worksHandler.loadFromDisk();
    }

    public void registerScheduleTask(MinecraftServer server) {
        ScheduleUtil.addJob(WorksScheduleJob.class, null, null, ScheduleUtil.CRON_EVERY_MINUTE, new JobDataMap() {
            {
                this.put(MinecraftServer.class.getName(), server);
            }
        });
    }

    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("works").executes(this::$works));
    }

    private int $works(@NotNull CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, player -> {
            new WorksGui(player,worksHandler.model().works, 0).open();
            return CommandHelper.Return.SUCCESS;
        });
    }

    public static class WorksScheduleJob implements Job {

        @Override
        public void execute(@NotNull JobExecutionContext context) {
            // save current works data
            MinecraftServer server = (MinecraftServer) context.getJobDetail().getJobDataMap().get(MinecraftServer.class.getName());
            if (server.isRunning()) {
                worksHandler.saveToDisk();
            }

            // run schedule method
            Set<Work> works = new HashSet<>();
            WorksCache.getBlockpos2works().values().forEach(works::addAll);
            WorksCache.getEntity2works().values().forEach(works::addAll);
            works.forEach(work -> {
                if (work instanceof ScheduleMethod sm) sm.onSchedule();
            });
        }
    }
}

