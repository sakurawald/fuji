package io.github.sakurawald.core.auxiliary.minecraft;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import lombok.experimental.UtilityClass;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class GuiHelper {

    public static int getRows(ScreenHandlerType<GenericContainerScreenHandler> screenHandlerType) {
        if (screenHandlerType == ScreenHandlerType.GENERIC_9X1) return 1;
        if (screenHandlerType == ScreenHandlerType.GENERIC_9X2) return 2;
        if (screenHandlerType == ScreenHandlerType.GENERIC_9X3) return 3;
        if (screenHandlerType == ScreenHandlerType.GENERIC_9X4) return 4;
        if (screenHandlerType == ScreenHandlerType.GENERIC_9X5) return 5;
        if (screenHandlerType == ScreenHandlerType.GENERIC_9X6) return 6;

        throw new IllegalArgumentException("Unknown screen handler type: " + screenHandlerType);
    }

    public static GuiElementInterface makeBarrier() {
        return new GuiElementBuilder()
            .setItem(Items.BARRIER)
            .hideTooltip()
            .build();
    }

    public static GuiElementInterface makeSlotPlaceholder() {
        return new GuiElementBuilder()
            .setItem(Items.GRAY_STAINED_GLASS_PANE)
            .hideTooltip()
            .build();
    }

    public static GuiElementBuilder makeSkullButton(String skullOwner) {
        return new GuiElementBuilder()
            .setItem(Items.PLAYER_HEAD)
            .setSkullOwner(skullOwner);
    }

    public static GuiElementBuilder makePreviousPageButton(ServerPlayerEntity player) {
        return makeSkullButton(Icon.PREVIOUS_PAGE_ICON)
            .setName(LocaleHelper.getTextByKey(player, "previous_page"));
    }

    public static GuiElementBuilder makeNextPageButton(ServerPlayerEntity player) {
        return makeSkullButton(Icon.NEXT_PAGE_ICON)
            .setName(LocaleHelper.getTextByKey(player, "next_page"));
    }

    public static GuiElementBuilder makeBackButton(ServerPlayerEntity player) {
        return makeSkullButton(Icon.PREVIOUS_PAGE_ICON)
            .setName(LocaleHelper.getTextByKey(player, "back"));
    }

    public static GuiElementBuilder makeSearchButton(ServerPlayerEntity player) {
        return new GuiElementBuilder()
            .setItem(Items.COMPASS)
            .setName(LocaleHelper.getTextByKey(player, "search"));
    }

    public static GuiElementBuilder makeAddButton(ServerPlayerEntity player) {
        return makeSkullButton(GuiHelper.Icon.PLUS_ICON)
            .setName(LocaleHelper.getTextByKey(player, "add"));
    }

    public static GuiElementBuilder makeHelpButton(ServerPlayerEntity player) {
        return new GuiElementBuilder()
            .setItem(Items.BOOK)
            .setName(LocaleHelper.getTextByKey(player, "help"));
    }

    public static GuiElementBuilder makePlayerPlaceholder() {
        return makeSkullButton(Icon.PLAYER_PLACEHOLDER_ICON);
    }

    public static void fill(@NotNull SimpleGui gui, ItemStack itemStack) {
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setSlot(i, itemStack);
        }
    }

    public boolean isInvalidSlotInPlayerInventory(int index) {
        return index == 41 || index == 42 || index == 43 || index == 44;
    }

    @SuppressWarnings("unused")
    public static class Item {
        public static final ItemStack PLACEHOLDER = Items.GRAY_STAINED_GLASS_PANE.getDefaultStack();
        public static final ItemStack EMPTY = Items.AIR.getDefaultStack();
    }

    public static class Icon {

        public static final String PREVIOUS_PAGE_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM3NjQ4YWU3YTU2NGE1Mjg3NzkyYjA1ZmFjNzljNmI2YmQ0N2Y2MTZhNTU5Y2U4YjU0M2U2OTQ3MjM1YmNlIn19fQ==";
        public static final String PLUS_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkZDIwYmU5MzUyMDk0OWU2Y2U3ODlkYzRmNDNlZmFlYjI4YzcxN2VlNmJmY2JiZTAyNzgwMTQyZjcxNiJ9fX0=";
        public static final String HEART_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzM2ZmViZWNhN2M0ODhhNjY3MWRjMDcxNjU1ZGRlMmExYjY1YzNjY2IyMGI2ZThlYWY5YmZiMDhlNjRiODAifX19";
        public static final String A_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY3ZDgxM2FlN2ZmZTViZTk1MWE0ZjQxZjJhYTYxOWE1ZTM4OTRlODVlYTVkNDk4NmY4NDk0OWM2M2Q3NjcyZSJ9fX0=";
        public static final String QUESTION_MARK_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNlYzg1YmM4MDYxYmRhM2UxZDQ5Zjc1NDQ2NDllNjVjODI3MmNhNTZmNzJkODM4Y2FmMmNjNDgxNmI2OSJ9fX0=";
        public static final String NEXT_PAGE_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE0ZjY4YzhmYjI3OWU1MGFiNzg2ZjlmYTU0Yzg4Y2E0ZWNmZTFlYjVmZDVmMGMzOGM1NGM5YjFjNzIwM2Q3YSJ9fX0=";
        public static final String PLAYER_PLACEHOLDER_ICON = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ5Y2M1OGFkMjVhMWFiMTZkMzZiYjVkNmQ0OTNjOGY1ODk4YzJiZjMwMmI2NGUzMjU5MjFjNDFjMzU4NjcifX19";
    }

}
