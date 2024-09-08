package io.github.sakurawald.module.initializer.gameplay.carpet.better_info;

import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;


public class BetterInfoInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("info").then(
                        dispatcher.findNode(List.of("data", "get", "entity"))
                )
        ));
    }

}
