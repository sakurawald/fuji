package io.github.sakurawald.module.initializer.command_event;

import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.service.command_executor.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class CommandEventInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> CommandExecutor.executeSpecializedCommand(newPlayer, Configs.configHandler.model().modules.command_event.event.after_player_respawn.command_list));

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> CommandExecutor.executeSpecializedCommand(player, Configs.configHandler.model().modules.command_event.event.after_player_change_world.command_list));

    }
}
