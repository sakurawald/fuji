package fun.sakurawald.module.display.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import fun.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ShulkerBoxBlock;

@Slf4j
public class InventoryDisplayGui extends DisplayGuiBuilder {

    private final Component title;
    private final NonNullList<ItemStack> armor = NonNullList.create();
    private final NonNullList<ItemStack> offhand = NonNullList.create();
    private final NonNullList<ItemStack> items = NonNullList.create();

    public InventoryDisplayGui(Component title, ServerPlayer player) {
        this.title = title;
        Inventory inventory = player.getInventory();
        inventory.armor.forEach(itemStack -> armor.add(itemStack.copy()));
        inventory.offhand.forEach(itemStack -> offhand.add(itemStack.copy()));
        inventory.items.forEach(itemStack -> items.add(itemStack.copy()));
    }

    @Override
    public SimpleGui build(ServerPlayer player) {
        /* construct base */
        SimpleGui gui = new SimpleGui(MenuType.GENERIC_9x6, player, false);
        gui.setLockPlayerInventory(true);
        gui.setTitle(this.title);

        for (int i = 0; i < LINE_SIZE * 2; i++) {
            gui.setSlot(i, new GuiElementBuilder().setItem(Items.PINK_STAINED_GLASS_PANE));
        }

        /* construct armor */
        for (int i = 1; i < 5; i++) {
            gui.setSlot(i, armor.get((5 - 1) - i));
        }

        /* construct offhand */
        MySlotClickCallback mySlotClickCallback = new MySlotClickCallback(gui, player);
        gui.setSlot(7, offhand.getFirst(), mySlotClickCallback);

        /* construct items */
        for (int i = LINE_SIZE * 5; i < LINE_SIZE * 6; i++) {
            gui.setSlot(i, items.get(i - LINE_SIZE * 5), mySlotClickCallback);
        }
        for (int i = LINE_SIZE * 2; i < LINE_SIZE * 5; i++) {
            gui.setSlot(i, items.get(i - LINE_SIZE), mySlotClickCallback);
        }
        return gui;
    }

    private record MySlotClickCallback(SimpleGui parentGui,
                                       ServerPlayer player) implements GuiElementInterface.ClickCallback {
        @Override
        public void click(int i, ClickType clickType, net.minecraft.world.inventory.ClickType clickType1, SlotGuiInterface slotGuiInterface) {
            ItemStack itemStack = slotGuiInterface.getSlot(i).getItemStack();
            if (itemStack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
                ShulkerBoxDisplayGui shulkerBoxDisplayGui = new ShulkerBoxDisplayGui(MessageUtil.ofVomponent(player, "display.gui.title", player.getGameProfile().getName()), itemStack, parentGui);
                shulkerBoxDisplayGui.build(player).open();
            }
        }
    }
}
