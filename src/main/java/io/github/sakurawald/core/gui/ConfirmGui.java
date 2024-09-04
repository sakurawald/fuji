package io.github.sakurawald.core.gui;

import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
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
