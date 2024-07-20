package io.github.sakurawald.util;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import lombok.experimental.UtilityClass;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

@UtilityClass
public class GuiUtil {

    public static class Item {
        public static final ItemStack PLACEHOLDER = Items.GRAY_STAINED_GLASS_PANE.getDefaultStack();
        public static final ItemStack EMPTY = Items.AIR.getDefaultStack();
    }

    public static GuiElementBuilder createPreviousPageButton(ServerPlayerEntity player) {
        return new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(MessageUtil.ofText(player, "previous_page"))
                .setSkullOwner(Icon.PREVIOUS_PAGE_ICON);
    }

    public static GuiElementBuilder createNextPageButton(ServerPlayerEntity player) {
        return new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(MessageUtil.ofText(player, "next_page"))
                .setSkullOwner(Icon.NEXT_PAGE_ICON);
    }

    public static GuiElementBuilder createBackButton(ServerPlayerEntity player) {
        return new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(MessageUtil.ofText(player, "back"))
                .setSkullOwner(Icon.PREVIOUS_PAGE_ICON);
    }

    public static GuiElementBuilder createSearchButton(ServerPlayerEntity player) {
        return new GuiElementBuilder()
                .setItem(Items.COMPASS)
                .setName(MessageUtil.ofText(player, "search"));
    }

    public static GuiElementBuilder createAddButton(ServerPlayerEntity player) {
        return new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(MessageUtil.ofText(player, "add"))
                .setSkullOwner(GuiUtil.Icon.PLUS_ICON);
    }

    public static GuiElementBuilder createHelpButton(ServerPlayerEntity player) {
        return new GuiElementBuilder()
                .setItem(Items.PLAYER_HEAD)
                .setName(MessageUtil.ofText(player, "help"))
                .setSkullOwner(Icon.HEART_ICON);
    }


    public static void fill(SimpleGui gui, ItemStack itemStack) {
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setSlot(i,itemStack );
        }
    }

    public static class Icon {

        public static final String PREVIOUS_PAGE_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM3NjQ4YWU3YTU2NGE1Mjg3NzkyYjA1ZmFjNzljNmI2YmQ0N2Y2MTZhNTU5Y2U4YjU0M2U2OTQ3MjM1YmNlIn19fQ==";
        public static final String PLUS_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkZDIwYmU5MzUyMDk0OWU2Y2U3ODlkYzRmNDNlZmFlYjI4YzcxN2VlNmJmY2JiZTAyNzgwMTQyZjcxNiJ9fX0=";
        public static final String HEART_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzM2ZmViZWNhN2M0ODhhNjY3MWRjMDcxNjU1ZGRlMmExYjY1YzNjY2IyMGI2ZThlYWY5YmZiMDhlNjRiODAifX19";
        public static final String A_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY3ZDgxM2FlN2ZmZTViZTk1MWE0ZjQxZjJhYTYxOWE1ZTM4OTRlODVlYTVkNDk4NmY4NDk0OWM2M2Q3NjcyZSJ9fX0=";
        public static final String QUESTION_MARK_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNlYzg1YmM4MDYxYmRhM2UxZDQ5Zjc1NDQ2NDllNjVjODI3MmNhNTZmNzJkODM4Y2FmMmNjNDgxNmI2OSJ9fX0=";
        public static final String NEXT_PAGE_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE0ZjY4YzhmYjI3OWU1MGFiNzg2ZjlmYTU0Yzg4Y2E0ZWNmZTFlYjVmZDVmMGMzOGM1NGM5YjFjNzIwM2Q3YSJ9fX0=";
    }

}
