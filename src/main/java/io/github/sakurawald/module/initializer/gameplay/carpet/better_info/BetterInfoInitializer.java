package io.github.sakurawald.module.initializer.gameplay.carpet.better_info;

import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;

import java.util.List;

public class BetterInfoInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("info").then(
                        dispatcher.findNode(List.of("data", "get", "entity"))
                )
        ));
    }

}
