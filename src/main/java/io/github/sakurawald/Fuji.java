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
    public static final Logger log = createLogger();
    public static final String MOD_ID = "fuji";
    public static final Path CONFIG_PATH = Path.of(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toString());
    public static MinecraftServer SERVER;

    public static Logger createLogger() {
        Logger logger = LogManager.getLogger("Fuji");
        try {
            String level = System.getProperty("fuji.level");
            Configurator.setLevel(logger, Level.getLevel(level));
        } catch (Exception e) {
            return logger;
        }
        return logger;
    }

    @Override
    public void onInitialize() {
        /* modules */
        ModuleManager.initializeModules();

        /* scheduler */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ScheduleUtil.startScheduler());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ScheduleUtil.shutdownScheduler());
    }
}