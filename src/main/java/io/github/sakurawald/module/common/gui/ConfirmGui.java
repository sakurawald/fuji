package io.github.sakurawald.module.common.gui;

import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ConfirmGui extends InputSignGui {
    public ConfirmGui(ServerPlayerEntity player) {
        super(player, "prompt.input.confirm");
    }

    @Override
    public void onClose() {
        if (!this.getLine(0).getString().equals("confirm")) {
            MessageHelper.sendActionBar(player, "operation.cancelled");
            return;
        }
        onConfirm();
    }

    public abstract void onConfirm();
}
