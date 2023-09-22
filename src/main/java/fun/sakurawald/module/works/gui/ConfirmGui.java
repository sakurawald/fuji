package fun.sakurawald.module.works.gui;

import fun.sakurawald.util.MessageUtil;
import net.minecraft.server.level.ServerPlayer;

import static fun.sakurawald.util.MessageUtil.ofString;

public abstract class ConfirmGui extends InputSignGui {
    public ConfirmGui(ServerPlayer player) {
        super(player, ofString(player, "prompt.input.confirm"));
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
