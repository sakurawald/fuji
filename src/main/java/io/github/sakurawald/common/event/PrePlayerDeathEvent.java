package io.github.sakurawald.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PrePlayerDeathEvent {
    Event<PrePlayerDeathEvent> EVENT = EventFactory.createArrayBacked(PrePlayerDeathEvent.class,
            (listeners) -> (player, damageSource) -> {
                for (PrePlayerDeathEvent listener : listeners) {
                    ActionResult result = listener.interact(player, damageSource);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult interact(ServerPlayerEntity player, DamageSource damageSource);
}
