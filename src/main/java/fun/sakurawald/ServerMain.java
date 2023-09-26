package fun.sakurawald;

import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.back.BackModule;
import fun.sakurawald.module.better_fake_player.BetterFakePlayerModule;
import fun.sakurawald.module.chat_style.ChatStyleModule;
import fun.sakurawald.module.config.ConfigModule;
import fun.sakurawald.module.deathlog.DeathLogModule;
import fun.sakurawald.module.main_stats.MainStatsModule;
import fun.sakurawald.module.pvp_toggle.PvpModule;
import fun.sakurawald.module.resource_world.ResourceWorldModule;
import fun.sakurawald.module.skin.command.SkinModule;
import fun.sakurawald.module.teleport_warmup.TeleportWarmupModule;
import fun.sakurawald.module.top_chunks.TopChunksModule;
import fun.sakurawald.module.tpa.TpaModule;
import fun.sakurawald.module.works.WorksModule;
import fun.sakurawald.module.world_downloader.WorldDownloaderModule;
import fun.sakurawald.module.zero_command_permission.ZeroCommandPermissionModule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

// better mini-motd
@Slf4j
public class ServerMain implements ModInitializer {
    public static final Path CONFIG_PATH = Path.of(FabricLoader.getInstance().getConfigDir().resolve("sakurawald").toString());
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

        /* config */
        ConfigManager.configWrapper.loadFromDisk();
        ConfigManager.chatWrapper.loadFromDisk();
        ConfigManager.pvpWrapper.loadFromDisk();
        ConfigManager.worksWrapper.loadFromDisk();

        /* register commands */
        CommandRegistrationCallback.EVENT.register(ConfigModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(PvpModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(ResourceWorldModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(TopChunksModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(BetterFakePlayerModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(ChatStyleModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(SkinModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(DeathLogModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(BackModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(TpaModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(WorksModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(WorldDownloaderModule::registerCommand);

        /* register events */
        ServerWorldEvents.UNLOAD.register(ResourceWorldModule::onWorldUnload);
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
                    ResourceWorldModule.loadWorlds(server);
                    ResourceWorldModule.registerScheduleTask(server);

                    MainStatsModule.updateMainStats();
                    MainStatsModule.registerScheduleTask(server);

                    ZeroCommandPermissionModule.alterCommandPermission(server);

                    BetterFakePlayerModule.registerScheduleTask(server);

                    WorksModule.registerScheduleTask(server);
                }
        );
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> getSCHEDULED_EXECUTOR_SERVICE().shutdown());

        ServerTickEvents.START_SERVER_TICK.register(TeleportWarmupModule::onServerTick);
    }
}