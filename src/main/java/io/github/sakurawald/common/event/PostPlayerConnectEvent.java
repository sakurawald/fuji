package io.github.sakurawald.common.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface PostPlayerConnectEvent {
    Event<PostPlayerConnectEvent> EVENT = EventFactory.createArrayBacked(PostPlayerConnectEvent.class,
            (listeners) -> (connection, player, commonListenerCookie) -> {
                for (PostPlayerConnectEvent listener : listeners) {
                    ActionResult result = listener.interact(connection, player, commonListenerCookie);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult interact(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData commonListenerCookie);
}
