package io.github.sakurawald.module.chat.display.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemDisplayGui extends DisplayGuiBuilder {

    private final Component title;
    private final ItemStack itemStack;

    public ItemDisplayGui(Component title, ItemStack itemStack) {
        this.title = title;
        this.itemStack = itemStack;
    }

    @Override
    public SimpleGui build(ServerPlayer player) {
        SimpleGui gui = new SimpleGui(MenuType.GENERIC_3x3, player, false);
        gui.setLockPlayerInventory(true);
        gui.setTitle(this.title);

        /* construct base */
        for (int i = 0; i < 9; i++) {
            gui.setSlot(i, new GuiElementBuilder().setItem(Items.PINK_STAINED_GLASS_PANE));
        }
        /* construct item */
        gui.setSlot(4, itemStack);
        return gui;
    }
}
