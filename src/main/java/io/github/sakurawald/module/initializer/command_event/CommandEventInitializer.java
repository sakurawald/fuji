package io.github.sakurawald.module.initializer.command_event;

import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.service.command_executor.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_event.config.model.CommandEventModel;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class CommandEventInitializer extends ModuleInitializer {

    public final ObjectConfigurationHandler<CommandEventModel> config = new ObjectConfigurationHandler<>("config.command_event.json", CommandEventModel.class);

    @Override
    public void onInitialize() {
        config.readStorage();

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> CommandExecutor.executeSpecializedCommand(newPlayer, config.getModel().event.after_player_respawn.command_list));

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> CommandExecutor.executeSpecializedCommand(player, config.getModel().event.after_player_change_world.command_list));

    }

    @Override
    public void onReload() {
        config.readStorage();
    }
}
