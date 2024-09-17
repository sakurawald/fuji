package io.github.sakurawald.module.initializer.works;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.config.handler.abst.ConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.manager.impl.scheduler.ScheduleManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.works.config.model.WorksModel;
import io.github.sakurawald.module.initializer.works.gui.WorksGui;
import io.github.sakurawald.module.initializer.works.job.WorksScheduleJob;
import io.github.sakurawald.module.initializer.works.structure.work.abst.Work;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.quartz.JobDataMap;

public class WorksInitializer extends ModuleInitializer {

    public static final ConfigurationHandler<WorksModel> worksHandler = new ObjectConfigurationHandler<>("works.json", WorksModel.class);

    @Override
    public void onInitialize() {
        ConfigurationHandler.registerTypeAdapter(Work.class, new Work.WorkTypeAdapter());
        worksHandler.readDisk();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> new WorksScheduleJob(new JobDataMap() {
            {
                this.put(MinecraftServer.class.getName(), server);
            }
        }, () -> ScheduleManager.CRON_EVERY_MINUTE).schedule());
    }

    @Override
    public void onReload() {
        worksHandler.readDisk();
    }

    @CommandNode("works")
    private int $works(@CommandSource ServerPlayerEntity player) {
        new WorksGui(player, worksHandler.getModel().works, 0).open();
        return CommandHelper.Return.SUCCESS;
    }

}

