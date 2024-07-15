package io.github.sakurawald.module.initializer.chat.display.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;


public class EnderChestDisplayGui extends DisplayGuiBuilder {

    private final Text title;
    private final DefaultedList<ItemStack> items = DefaultedList.of();

    public EnderChestDisplayGui(Text title, ServerPlayerEntity serverPlayer) {
        this.title = title;
        serverPlayer.getEnderChestInventory().getHeldStacks().forEach(itemStack -> this.items.add(itemStack.copy()));
    }

    @Override
    public SimpleGui build(ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X4, player, false);
        gui.setLockPlayerInventory(true);
        gui.setTitle(this.title);

        /* construct base  */
        for (int i = 0; i < 9; i++) {
            gui.setSlot(i, new GuiElementBuilder().setItem(Items.PINK_STAINED_GLASS_PANE));
        }
        gui.setSlot(4, Items.ENDER_CHEST.getDefaultStack());

        /* construct items */
        SlotClickForDeeperDisplayCallback slotClickForDeeperDisplayCallback = new SlotClickForDeeperDisplayCallback(gui, player);
        for (int i = 0; i < this.items.size(); i++) {
            $setSlot(gui, LINE_SIZE + i, this.items.get(i), slotClickForDeeperDisplayCallback);
        }
        return gui;
    }
}
