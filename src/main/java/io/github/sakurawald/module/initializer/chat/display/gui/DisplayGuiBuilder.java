package io.github.sakurawald.module.initializer.chat.display.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import io.github.sakurawald.core.auxiliary.minecraft.LanguageHelper;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public abstract class DisplayGuiBuilder {

    protected static final int LINE_SIZE = 9;

    protected static void $setSlot(@NotNull SimpleGui gui, int i, @NotNull ItemStack itemStack, SlotClickForDeeperDisplayCallback slotClickForDeeperDisplayCallback) {
        GuiElementBuilder guiElementBuilder = GuiElementBuilder.from(itemStack).setCallback(slotClickForDeeperDisplayCallback);
        if (isShulkerBox(itemStack)) {
            guiElementBuilder.addLoreLine(LanguageHelper.getTextByKey(gui.getPlayer(), "display.click.prompt"));
        }
        gui.setSlot(i, guiElementBuilder.build());
    }

    public static boolean isShulkerBox(@NotNull ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock;
    }

    public abstract SimpleGui build(ServerPlayerEntity player);

    protected record SlotClickForDeeperDisplayCallback(SimpleGui parentGui,
                                                       ServerPlayerEntity player) implements GuiElementInterface.ClickCallback {
        @Override
        public void click(int i, ClickType clickType, net.minecraft.screen.slot.SlotActionType clickType1, @NotNull SlotGuiInterface slotGuiInterface) {
            ItemStack itemStack = slotGuiInterface.getSlot(i).getItemStack();
            if (isShulkerBox(itemStack)) {
                ShulkerBoxDisplayGui shulkerBoxDisplayGui = new ShulkerBoxDisplayGui(LanguageHelper.getTextByKey(player, "display.gui.title", player.getGameProfile().getName()), itemStack, parentGui);
                shulkerBoxDisplayGui.build(player).open();
            }
        }
    }
}
