package fun.sakurawald.module.display;

import fun.sakurawald.module.AbstractModule;
import fun.sakurawald.module.display.gui.DisplayGuiBuilder;
import fun.sakurawald.module.display.gui.InventoryDisplayGui;
import fun.sakurawald.module.display.gui.ItemDisplayGui;
import fun.sakurawald.module.display.gui.ShulkerBoxDisplayGui;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings({"SameReturnValue"})
public class DisplayModule extends AbstractModule {

    private final SoftReferenceMap<String, DisplayGuiBuilder> uuid2gui = new SoftReferenceMap<>();

    public String createInventoryDisplay(@NotNull ServerPlayer player) {
        Component title = MessageUtil.ofVomponent(player, "display.gui.title", player.getGameProfile().getName());
        InventoryDisplayGui inventoryDisplayGui = new InventoryDisplayGui(title, player);
        String uuid = UUID.randomUUID().toString();
        uuid2gui.put(uuid, inventoryDisplayGui);
        return uuid;
    }

    public String createItemDisplay(@NotNull ServerPlayer player) {
        /* new object */
        DisplayGuiBuilder displayGuiBuilder;
        Component title = MessageUtil.ofVomponent(player, "display.gui.title", player.getGameProfile().getName());
        ItemStack itemStack = player.getMainHandItem().copy();
        if (itemStack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) {
            // shulker-box item
            displayGuiBuilder = new ShulkerBoxDisplayGui(title, itemStack, null);
        } else {
            // non-shulker-box item
            displayGuiBuilder = new ItemDisplayGui(title, itemStack);
        }

        /* put object */
        String uuid = UUID.randomUUID().toString();
        uuid2gui.put(uuid, displayGuiBuilder);
        return uuid;
    }

    public void viewDisplay(@NotNull ServerPlayer player, String displayUUID) {
        DisplayGuiBuilder displayGuiBuilder = uuid2gui.get(displayUUID);
        if (displayGuiBuilder == null) {
            MessageUtil.sendMessage(player, "display.invalid");
            return;
        }
        displayGuiBuilder.build(player).open();
    }
}
