package io.github.sakurawald.common.event;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface PreCommandExecuteEvent {
    Event<PreCommandExecuteEvent> EVENT = EventFactory.createArrayBacked(PreCommandExecuteEvent.class,
            (listeners) -> (parseResults, string) -> {
                for (PreCommandExecuteEvent listener : listeners) {
                    ActionResult result = listener.interact(parseResults, string);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return  ActionResult.PASS;
            });

    ActionResult interact(ParseResults<ServerCommandSource> parseResults, String string);
}
