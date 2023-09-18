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
import fun.sakurawald.module.stronger_player_list.PlayerListAccessor;
import fun.sakurawald.module.teleport_warmup.TeleportWarmupModule;
import fun.sakurawald.module.top_chunks.TopChunksModule;
import fun.sakurawald.module.tpa.TpaModule;
import fun.sakurawald.module.zero_command_permission.ZeroCommandPermissionModule;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

@Slf4j
public class ServerMain implements ModInitializer {
    public static final Path CONFIG_PATH = Path.of(FabricLoader.getInstance().getConfigDir().resolve("sakurawald").toString());
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

        /* register events */
        ServerWorldEvents.UNLOAD.register(ResourceWorldModule::onWorldUnload);
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
                    // fix: PlayerList CME BUG
                    ((PlayerListAccessor) server.getPlayerList()).patchStrongerPlayerList();

                    ResourceWorldModule.loadWorlds(server);
                    ResourceWorldModule.registerScheduleTask(server);

                    MainStatsModule.updateMOTD();
                    MainStatsModule.registerScheduleTask(server);

                    ZeroCommandPermissionModule.alterCommandPermission(server);

                    BetterFakePlayerModule.registerScheduleTask(server);
                }
        );

        ServerTickEvents.START_SERVER_TICK.register(TeleportWarmupModule::onServerTick);
    }
}