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
// TODO: interactive command (sign)
// TODO: /tppos module
// TODO: command alias module (test priority with ZeroPermissionModule)
// TODO: playtime(every/for) rewards and rank like module
// TODO: kit module
// TODO: luckperms context calculate module
// TODO: placeholder module
// TODO: warmup module
// TODO: wastebin module
public class ServerMain implements ModInitializer {
    public static final String MOD_ID = "sakurawald";
    public static final Path CONFIG_PATH = Path.of(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toString());
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        /* set server: set first because server started event priority */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);

        /* modules */
        ModuleManager.initializeModules();

        /* scheduler */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ScheduleUtil.startScheduler());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ScheduleUtil.shutdownScheduler());
    }
}