package io.github.sakurawald.module.initializer.works;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.WorksModel;
import io.github.sakurawald.module.common.gui.InputSignGui;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.works.gui.WorksGui;
import io.github.sakurawald.module.initializer.works.work_type.NonProductionWork;
import io.github.sakurawald.module.initializer.works.work_type.ProductionWork;
import io.github.sakurawald.module.initializer.works.work_type.Work;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.GuiUtil;
import io.github.sakurawald.util.MessageUtil;
import io.github.sakurawald.util.ScheduleUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.HashSet;
import java.util.List;
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
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("works").executes(this::$works));
    }

    private int $works(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            new WorksGui(player,worksHandler.model().works).open();
            return Command.SINGLE_SUCCESS;
        });
    }

    public static class WorksScheduleJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
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

