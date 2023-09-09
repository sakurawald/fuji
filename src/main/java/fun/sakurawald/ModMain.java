package fun.sakurawald;

import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.custom_stats.CustomStatisticsModule;
import fun.sakurawald.module.pvp_toggle.PvpModule;
import fun.sakurawald.module.pvp_toggle.PvpWhitelist;
import fun.sakurawald.module.resource_world.ResourceWorldModule;
import fun.sakurawald.module.teleport_warmup.TeleportWarmupModule;
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

// TODO: simple chat
// TODO: xht bug
public class ModMain implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SakuraWald");
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        LOGGER.info("Loading sakurawald...");

        /* server */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
        });

        /* config */
        ConfigManager.configWrapper.loadFromDisk();

        /* pvp toggle */
        PvpWhitelist.create(new File("pvp_whitelist.json"));
        CommandRegistrationCallback.EVENT.register(PvpModule::registerCommand);

        /* resource world */
        CommandRegistrationCallback.EVENT.register(ResourceWorldModule::registerCommand);
        ServerLifecycleEvents.SERVER_STARTED.register(ResourceWorldModule::loadWorlds);
        ServerWorldEvents.UNLOAD.register(ResourceWorldModule::onWorldUnload);
        ServerLifecycleEvents.SERVER_STARTED.register(ResourceWorldModule::registerScheduleTask);

        /* custom custom_stats*/
        CustomStatisticsModule.registerCustomStats();
        CustomStatisticsModule.mergeServerStats();
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            CustomStatisticsModule.registerScheduleTask(server);
            CustomStatisticsModule.updateMOTD();
        });

        /* teleport warmup */
        ServerTickEvents.START_SERVER_TICK.register(TeleportWarmupModule::onServerTick);

        /* zero command permission */
        ServerLifecycleEvents.SERVER_STARTED.register(ZeroCommandPermissionModule::alterCommandPermission);
    }

}