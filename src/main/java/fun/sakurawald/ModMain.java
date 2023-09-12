package fun.sakurawald;

import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.better_fake_player.BetterFakePlayerModule;
import fun.sakurawald.module.chat_style.ChatStyleModule;
import fun.sakurawald.module.config.ConfigModule;
import fun.sakurawald.module.main_stats.CustomStatisticsModule;
import fun.sakurawald.module.pvp_toggle.PvpModule;
import fun.sakurawald.module.pvp_toggle.PvpWhitelist;
import fun.sakurawald.module.resource_world.ResourceWorldModule;
import fun.sakurawald.module.teleport_warmup.TeleportWarmupModule;
import fun.sakurawald.module.top_chunks.TopChunksModule;
import fun.sakurawald.module.zero_command_permission.ZeroCommandPermissionModule;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

// TODO: res -> xht bug
// TODO: res -> teleport to home first
public class ModMain implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SakuraWald");
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        LOGGER.info("onInitialize()");

        /* server instance */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);

        /* config */
        PvpWhitelist.create(new File("pvp_whitelist.json"));
        ConfigManager.configWrapper.loadFromDisk();
        ConfigManager.chatWrapper.loadFromDisk();

        /* register commands */
        CommandRegistrationCallback.EVENT.register(ConfigModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(PvpModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(ResourceWorldModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(TopChunksModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(BetterFakePlayerModule::registerCommand);
        CommandRegistrationCallback.EVENT.register(ChatStyleModule::registerCommand);

        /* register events */
        ServerWorldEvents.UNLOAD.register(ResourceWorldModule::onWorldUnload);
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
                    ResourceWorldModule.loadWorlds(server);
                    ResourceWorldModule.registerScheduleTask(server);

                    CustomStatisticsModule.updateMOTD();
                    CustomStatisticsModule.registerScheduleTask(server);

                    ZeroCommandPermissionModule.alterCommandPermission(server);

                    BetterFakePlayerModule.registerScheduleTask(server);
                }
        );

        ServerTickEvents.START_SERVER_TICK.register(TeleportWarmupModule::onServerTick);
    }
}