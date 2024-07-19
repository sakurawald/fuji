package io.github.sakurawald.module.common.gui;

import io.github.sakurawald.util.MessageUtil;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ConfirmGui extends InputSignGui {
    public ConfirmGui(ServerPlayerEntity player) {
        super(player, MessageUtil.getString(player, "prompt.input.confirm"));
    }

    @Override
    public void onClose() {
        if (!this.getLine(0).getString().equals("confirm")) {
            MessageUtil.sendActionBar(player, "operation.cancelled");
            return;
        }
        onConfirm();
    }

    public abstract void onConfirm();
}
