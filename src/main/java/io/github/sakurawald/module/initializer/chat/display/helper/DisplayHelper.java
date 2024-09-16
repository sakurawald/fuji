package io.github.sakurawald.module.initializer.chat.display.helper;

import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.module.initializer.chat.display.gui.*;
import io.github.sakurawald.module.initializer.chat.display.structure.SoftReferenceMap;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DisplayHelper {

    private static final SoftReferenceMap<String, DisplayGuiBuilder> uuid2gui = new SoftReferenceMap<>();

    public static String createInventoryDisplay(@NotNull ServerPlayerEntity player) {
        Text title = LocaleHelper.getTextByKey(player, "display.gui.title", player.getGameProfile().getName());
        String uuid = UUID.randomUUID().toString();
        uuid2gui.put(uuid, new InventoryDisplayGui(title, player));
        return uuid;
    }

    public static String createEnderChestDisplay(@NotNull ServerPlayerEntity player) {
        Text title = LocaleHelper.getTextByKey(player, "display.gui.title", player.getGameProfile().getName());
        String uuid = UUID.randomUUID().toString();
        uuid2gui.put(uuid, new EnderChestDisplayGui(title, player));
        return uuid;
    }

    public static String createItemDisplay(@NotNull ServerPlayerEntity player) {
        /* new object */
        DisplayGuiBuilder displayGuiBuilder;
        Text title = LocaleHelper.getTextByKey(player, "display.gui.title", player.getGameProfile().getName());
        ItemStack itemStack = player.getMainHandStack().copy();
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

    public static void viewDisplay(@NotNull ServerPlayerEntity player, String displayUUID) {
        DisplayGuiBuilder displayGuiBuilder = uuid2gui.get(displayUUID);
        if (displayGuiBuilder == null) {
            LocaleHelper.sendMessageByKey(player, "display.invalid");
            return;
        }
        displayGuiBuilder.build(player).open();
    }

}
