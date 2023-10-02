package fun.sakurawald.module.display.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static fun.sakurawald.module.works.WorksModule.PREVIOUS_PAGE_ICON;
import static fun.sakurawald.util.MessageUtil.ofVomponent;

@Slf4j
public class ShulkerBoxDisplayGui extends DisplayGuiBuilder {

    private final Component title;
    private final ItemStack itemStack;
    private final SimpleGui parentGui;

    public ShulkerBoxDisplayGui(Component title, ItemStack itemStack, SimpleGui parentGui) {
        this.title = title;
        this.itemStack = itemStack;
        this.parentGui = parentGui;
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
        gui.setSlot(4, itemStack);
        if (this.parentGui != null) {
            gui.setSlot(LINE_SIZE - 1, new GuiElementBuilder()
                    .setItem(Items.PLAYER_HEAD)
                    .setName(ofVomponent(player, "back"))
                    .setSkullOwner(PREVIOUS_PAGE_ICON)
                    .setCallback(parentGui::open));
        }

        /* construct items */
        CompoundTag blockEntityData = BlockItem.getBlockEntityData(itemStack);
        if (blockEntityData != null) {
            ListTag items = (ListTag) blockEntityData.get("Items");
            if (items == null) return gui;
            items.forEach(tag -> {
                CompoundTag compoundTag = (CompoundTag) tag;
                int slot = compoundTag.getInt("Slot");
                ItemStack itemStack = ItemStack.of(compoundTag);
                gui.setSlot(LINE_SIZE + slot, itemStack);
            });
        }
        return gui;
    }
}
