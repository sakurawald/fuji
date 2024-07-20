package io.github.sakurawald.module.common.gui;

import eu.pb4.sgui.api.gui.SignGui;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

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

    private String reduce() {
        StringBuilder sb = new StringBuilder();
        sb.delete(0, sb.length());
        for (int i = 0; i < 4; i++) {
            sb.append(this.getLine(i).getString().trim());
        }
        return sb.toString().trim();
    }

    public @Nullable String getInput() {
        String lines = reduce();
        if (lines.isBlank()) return null;
        return lines;
    }

    public @NotNull String getInputOrEmpty() {
        String lines = reduce();
        if (lines.isBlank()) return "";
        return lines;
    }
}
