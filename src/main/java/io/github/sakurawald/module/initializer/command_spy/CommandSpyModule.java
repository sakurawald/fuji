package io.github.sakurawald.module.initializer.command_spy;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.common.event.PreCommandExecuteEvent;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class CommandSpyModule extends ModuleInitializer {
    @Override
    public void onInitialize() {
        PreCommandExecuteEvent.EVENT.register((parseResults, string) -> {
            ServerPlayerEntity player = parseResults.getContext().getSource().getPlayer();
            if (player == null) return ActionResult.PASS;

            // fix: fabric console will not log the command issue
            Fuji.LOGGER.info("{} issued server command: {}", player.getGameProfile().getName(), string);

            return ActionResult.PASS;
        });
    }
}
