package io.github.sakurawald.module.initializer.works.gui;

import eu.pb4.sgui.api.gui.SignGui;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;

public class InputSignGui extends SignGui {

    public InputSignGui(ServerPlayerEntity player, String promptKey) {
        super(player);
        this.setSignType(Blocks.CHERRY_WALL_SIGN);
        this.setColor(DyeColor.BLACK);
        if (promptKey != null) {
            this.setLine(3, MessageUtil.ofText(promptKey));
        }
        this.setAutoUpdate(false);
    }

    public String combineAllLines() {
        StringBuilder sb = new StringBuilder();
        sb.delete(0, sb.length());
        for (int i = 0; i < 4; i++) {
            sb.append(this.getLine(i).getString().trim());
        }
        return sb.toString().trim();
    }

    public String combineAllLinesReturnNull() {
        String lines = combineAllLines();
        if (lines.isBlank()) return null;
        return lines;
    }
}
