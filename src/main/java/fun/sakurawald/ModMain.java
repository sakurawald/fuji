package fun.sakurawald;

import fun.sakurawald.pvp_toggle.PvpModule;
import fun.sakurawald.pvp_toggle.PvpWhitelist;
import fun.sakurawald.resource_world.ResourceWorldModule;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ModMain implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("sakurawald");


    @Override
    public void onInitialize() {
        LOGGER.info("Loading sakurawald...");

        /* pvp toggle */
        PvpWhitelist.create(new File("pvp_whitelist.json"));
        CommandRegistrationCallback.EVENT.register(PvpModule::registerCommand);

        /* resource world */
        CommandRegistrationCallback.EVENT.register(ResourceWorldModule::registerCommand);
        ServerLifecycleEvents.SERVER_STARTED.register(ResourceWorldModule::loadWorlds);
        ServerWorldEvents.UNLOAD.register(ResourceWorldModule::onWorldUnload);
        ServerLifecycleEvents.SERVER_STARTED.register(ResourceWorldModule::registerScheduleTask);
    }

}