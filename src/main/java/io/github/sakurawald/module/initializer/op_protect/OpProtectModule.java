package io.github.sakurawald.module.initializer.op_protect;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.common.event.PrePlayerDisconnectEvent;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.util.ActionResult;

public class OpProtectModule extends ModuleInitializer {

    @Override
    public void onInitialize() {
        PrePlayerDisconnectEvent.EVENT.register(((player, disconnectionInfo) -> {
            if (Fuji.SERVER.getPlayerManager().isOperator(player.getGameProfile())) {
                Fuji.LOGGER.info("op protect -> deop {}", player.getGameProfile().getName());
                Fuji.SERVER.getPlayerManager().removeFromOperators(player.getGameProfile());
            }
            return ActionResult.PASS;
        }));
    }
}
