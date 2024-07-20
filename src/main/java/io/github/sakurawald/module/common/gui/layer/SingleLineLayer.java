package io.github.sakurawald.module.common.gui.layer;

import eu.pb4.sgui.api.gui.layered.Layer;
import io.github.sakurawald.util.GuiUtil;
import net.minecraft.item.ItemStack;

public class SingleLineLayer extends Layer {

    public SingleLineLayer() {
        super(1, 9);
    }

    public SingleLineLayer(ItemStack itemStack) {
        this();
        for (int i = 0; i < this.getWidth(); i++) {
            this.addSlot(itemStack);
        }
    }

}
