package io.github.sakurawald;

import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.ScheduleUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

// TODO: remote workbench
// TODO: warmup module
// TODO: placeholder module
// TODO: /tppos module
// TODO: command alias module (test priority with ZeroPermissionModule)
// TODO: playtime(every/for) rewards and rank like module
// TODO: kit module
// TODO: luckperms context calculate module
// TODO: /invsee module

public class Fuji implements ModInitializer {
    public static final String MOD_ID = "fuji";
    public static final Logger LOGGER = LogUtil.createLogger("Fuji");
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toAbsolutePath();
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        /* modules */
        ModuleManager.initializeModules();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ModuleManager.reportModules());

        /* scheduler */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ScheduleUtil.startScheduler());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ScheduleUtil.triggerJobs(ConfigHandler.ConfigHandlerAutoSaveJob.class.getName());
            ScheduleUtil.shutdownScheduler();
        });
    }
}