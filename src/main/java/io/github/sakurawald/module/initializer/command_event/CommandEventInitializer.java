package io.github.sakurawald.module.initializer.command_event;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.structure.CommandExecuter;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

@Slf4j
public class CommandEventInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            CommandExecuter.executeCommandsAsConsoleWithContext(newPlayer, Configs.configHandler.model().modules.command_event.event.after_player_respawn.command_list);
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            CommandExecuter.executeCommandsAsConsoleWithContext(player, Configs.configHandler.model().modules.command_event.event.after_player_change_world.command_list);
        });

    }
}
