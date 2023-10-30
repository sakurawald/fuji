package io.github.sakurawald.module.chat.display.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


public class EnderChestDisplayGui extends DisplayGuiBuilder {

    private final Component title;
    private final NonNullList<ItemStack> items = NonNullList.create();

    public EnderChestDisplayGui(Component title, ServerPlayer serverPlayer) {
        this.title = title;
        serverPlayer.getEnderChestInventory().items.forEach(itemStack -> this.items.add(itemStack.copy()));
    }

    @Override
    public SimpleGui build(ServerPlayer player) {
        SimpleGui gui = new SimpleGui(MenuType.GENERIC_9x4, player, false);
        gui.setLockPlayerInventory(true);
        gui.setTitle(this.title);

        /* construct base  */
        for (int i = 0; i < 9; i++) {
            gui.setSlot(i, new GuiElementBuilder().setItem(Items.PINK_STAINED_GLASS_PANE));
        }
        gui.setSlot(4, Items.ENDER_CHEST.getDefaultInstance());

        /* construct items */
        SlotClickForDeeperDisplayCallback slotClickForDeeperDisplayCallback = new SlotClickForDeeperDisplayCallback(gui, player);
        for (int i = 0; i < this.items.size(); i++) {
            $setSlot(gui, LINE_SIZE + i, this.items.get(i), slotClickForDeeperDisplayCallback);
        }
        return gui;
    }
}
