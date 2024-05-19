package io.github.sakurawald.module.initializer.chat.display.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.util.GuiUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;


public class ShulkerBoxDisplayGui extends DisplayGuiBuilder {

    private final Text title;
    private final ItemStack itemStack;
    private final SimpleGui parentGui;

    public ShulkerBoxDisplayGui(Text title, ItemStack itemStack, SimpleGui parentGui) {
        this.title = title;
        this.itemStack = itemStack;
        this.parentGui = parentGui;
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
        gui.setSlot(4, itemStack);
        if (this.parentGui != null) {
            gui.setSlot(LINE_SIZE - 1, new GuiElementBuilder()
                    .setItem(Items.PLAYER_HEAD)
                    .setName(MessageUtil.ofVomponent(player, "back"))
                    .setSkullOwner(GuiUtil.PREVIOUS_PAGE_ICON)
                    .setCallback(parentGui::open));
        }

        /* construct items */
        player.sendMessage(Text.literal(itemStack.getComponents().toString()));

        ContainerComponent containerComponent = itemStack.get(DataComponentTypes.CONTAINER);

        if (containerComponent != null) {
            var counter = new Object() {
                int offset = 0;
            };
            containerComponent.stream().forEach(item -> {
                gui.setSlot(LINE_SIZE + counter.offset, item.copy());
                counter.offset++;
            });
        }

        return gui;
    }
}
