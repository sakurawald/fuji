package io.github.sakurawald.core.gui;

import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ConfirmGui extends InputSignGui {

    public ConfirmGui(ServerPlayerEntity player) {
        super(player, LocaleHelper.getTextWithKeyword(player, "prompt.input.confirm", "confirm"));
    }

    @Override
    public void onClose() {
        String string = LocaleHelper.getKeywordValue(getPlayer(), "confirm");
        if (!this.getLine(0).getString().equals(string)) {
            LocaleHelper.sendActionBarByKey(player, "operation.cancelled");
            return;
        }
        onConfirm();
    }

    public abstract void onConfirm();
}
