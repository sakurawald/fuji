package io.github.sakurawald.module.initializer.works;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.WorksModel;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.common.manager.scheduler.ScheduleManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.works.gui.WorksGui;
import io.github.sakurawald.module.initializer.works.work_type.Work;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
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
        Managers.getScheduleManager().scheduleJob(WorksScheduleJob.class, ScheduleManager.CRON_EVERY_MINUTE, new JobDataMap() {
            {
                this.put(MinecraftServer.class.getName(), server);
            }
        });
    }

    @Command("works")
    private int $works(@CommandSource ServerPlayerEntity player) {
        new WorksGui(player, worksHandler.model().works, 0).open();
        return CommandHelper.Return.SUCCESS;
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

