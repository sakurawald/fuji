package fun.sakurawald;

import fun.sakurawald.module.ModuleManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


// https://github.com/astei/krypton
// https://github.com/RelativityMC/VMP-fabric
// https://github.com/RelativityMC/C2ME-fabric
// https://github.com/embeddedt/ModernFix
// https://modrinth.com/mod/servercore
// https://www.curseforge.com/minecraft/mc-mods/starlight

@Slf4j
public class ServerMain implements ModInitializer {
    public static final Path CONFIG_PATH = Path.of(FabricLoader.getInstance().getConfigDir().resolve("sakurawald").toString());
    public static final String MOD_ID = "sakurawald";
    @Getter
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(runnable -> {
        var thread = new Thread(runnable, "SakuraWald Schedule Thread");
        thread.setUncaughtExceptionHandler((t, e) -> log.error("Exception in sakurawald schedule thread", e));
        return thread;
    });
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        log.info("onInitialize()");

        /* server instance */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);

        /* modules */
        ModuleManager.initializeModules();

        /* scheduler */
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> getSCHEDULED_EXECUTOR_SERVICE().shutdown());
    }
}