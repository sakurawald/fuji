package io.github.sakurawald;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.util.ScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;


@Slf4j
// TODO: soft-depend spark
public class ServerMain implements ModInitializer {
    public static final Path CONFIG_PATH = Path.of(FabricLoader.getInstance().getConfigDir().resolve("sakurawald").toString());
    public static final String MOD_ID = "sakurawald";
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        /* server instance */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);

        /* modules */
        ModuleManager.initializeModules();

        /* scheduler */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ScheduleUtil.startScheduler());
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> ScheduleUtil.shutdownScheduler());
    }
}