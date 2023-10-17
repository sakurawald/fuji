package io.github.sakurawald.module.display;

import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.module.display.gui.*;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings({"SameReturnValue"})
public class DisplayModule extends AbstractModule {

    private final SoftReferenceMap<String, DisplayGuiBuilder> uuid2gui = new SoftReferenceMap<>();

    public String createInventoryDisplay(@NotNull ServerPlayer player) {
        Component title = MessageUtil.ofVomponent(player, "display.gui.title", player.getGameProfile().getName());
        String uuid = UUID.randomUUID().toString();
        uuid2gui.put(uuid, new InventoryDisplayGui(title, player));
        return uuid;
    }

    public String createEnderChestDisplay(@NotNull ServerPlayer player) {
        Component title = MessageUtil.ofVomponent(player, "display.gui.title", player.getGameProfile().getName());
        String uuid = UUID.randomUUID().toString();
        uuid2gui.put(uuid, new EnderChestDisplayGui(title, player));
        return uuid;
    }

    public String createItemDisplay(@NotNull ServerPlayer player) {
        /* new object */
        DisplayGuiBuilder displayGuiBuilder;
        Component title = MessageUtil.ofVomponent(player, "display.gui.title", player.getGameProfile().getName());
        ItemStack itemStack = player.getMainHandItem().copy();
        if (DisplayGuiBuilder.isShulkerBox(itemStack)) {
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


    @Override
    public Supplier<Boolean> enableModule() {
        return () -> true;
    }
}
