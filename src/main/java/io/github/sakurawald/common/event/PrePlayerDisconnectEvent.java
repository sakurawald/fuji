package io.github.sakurawald.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PrePlayerDisconnectEvent {
 
    Event<PrePlayerDisconnectEvent> EVENT = EventFactory.createArrayBacked(PrePlayerDisconnectEvent.class,
        (listeners) -> (player, disconnectionInfo) -> {
            for (PrePlayerDisconnectEvent listener : listeners) {
                ActionResult result = listener.interact(player, disconnectionInfo);

                if(result != ActionResult.PASS) {
                    return result;
                }
            }
 
        return ActionResult.PASS;
    });
 
    ActionResult interact(ServerPlayerEntity player, DisconnectionInfo disconnectionInfo);
}