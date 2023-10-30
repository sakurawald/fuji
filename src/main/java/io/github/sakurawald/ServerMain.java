package io.github.sakurawald;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.util.ScheduleUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.nio.file.Path;


// TODO: placeholder module
// TODO: /tppos module
// TODO: command alias module (test priority with ZeroPermissionModule)
// TODO: playtime(every/for) rewards and rank like module
// TODO: kit module
// TODO: luckperms context calculate module
// TODO: warmup module
// TODO: wastebin module
// TODO: join and leave message
// TODO: /invsee module
// TODO: logger level maps

public class ServerMain implements ModInitializer {
    public static final Logger log = createLogger();
    public static final String MOD_ID = "sakurawald";
    public static final Path CONFIG_PATH = Path.of(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toString());
    public static MinecraftServer SERVER;

    public static Logger createLogger() {
        Logger logger = LogManager.getLogger("SakuraWald");
        try {
            String level = System.getProperty("sakurawald.level");
            Configurator.setLevel(logger, Level.getLevel(level));
        } catch (Exception e) {
            return logger;
        }
        return logger;
    }

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