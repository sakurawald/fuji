package io.github.sakurawald.module.works.gui;

import eu.pb4.sgui.api.gui.SignGui;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;

public class InputSignGui extends SignGui {

    public InputSignGui(ServerPlayer player, String promptKey) {
        super(player);
        this.setSignType(Blocks.CHERRY_WALL_SIGN);
        this.setColor(DyeColor.BLACK);
        if (promptKey != null) {
            this.setLine(3, MessageUtil.ofVomponent(promptKey));
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
