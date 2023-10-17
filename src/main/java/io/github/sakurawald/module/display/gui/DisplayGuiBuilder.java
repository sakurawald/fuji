package io.github.sakurawald.module.display.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public abstract class DisplayGuiBuilder {

    protected static final int LINE_SIZE = 9;

    protected static void $setSlot(SimpleGui gui, int i, ItemStack itemStack, SlotClickForDeeperDisplayCallback slotClickForDeeperDisplayCallback) {
        GuiElementBuilder guiElementBuilder = GuiElementBuilder.from(itemStack).setCallback(slotClickForDeeperDisplayCallback);
        if (isShulkerBox(itemStack)) {
            guiElementBuilder.addLoreLine(MessageUtil.ofVomponent(gui.getPlayer(), "display.click.prompt"));
        }
        gui.setSlot(i, guiElementBuilder.build());
    }

    public static boolean isShulkerBox(ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock;
    }

    public abstract SimpleGui build(ServerPlayer player);

    protected record SlotClickForDeeperDisplayCallback(SimpleGui parentGui,
                                                       ServerPlayer player) implements GuiElementInterface.ClickCallback {
        @Override
        public void click(int i, ClickType clickType, net.minecraft.world.inventory.ClickType clickType1, SlotGuiInterface slotGuiInterface) {
            ItemStack itemStack = slotGuiInterface.getSlot(i).getItemStack();
            if (isShulkerBox(itemStack)) {
                ShulkerBoxDisplayGui shulkerBoxDisplayGui = new ShulkerBoxDisplayGui(MessageUtil.ofVomponent(player, "display.gui.title", player.getGameProfile().getName()), itemStack, parentGui);
                shulkerBoxDisplayGui.build(player).open();
            }
        }
    }
}
