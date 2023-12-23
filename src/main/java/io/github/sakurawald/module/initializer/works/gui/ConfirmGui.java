package io.github.sakurawald.module.initializer.works.gui;

import io.github.sakurawald.util.MessageUtil;
import net.minecraft.server.level.ServerPlayer;

public abstract class ConfirmGui extends InputSignGui {
    public ConfirmGui(ServerPlayer player) {
        super(player, MessageUtil.ofString(player, "prompt.input.confirm"));
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
