package mod.fuji.core.gui.structure;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class GuiItems {

    public static @NotNull Item getGreenStainedGlassItem() {
        #if MC_VER < MC_26_2
        return Items.GREEN_STAINED_GLASS;
        #elif MC_VER >= MC_26_2
        return Items.STAINED_GLASS.pick(DyeColor.GREEN);
        #endif
    }

    public static @NotNull Item getRedStainedGlassItem() {
        #if MC_VER < MC_26_2
        return Items.RED_STAINED_GLASS;
        #elif MC_VER >= MC_26_2
        return Items.STAINED_GLASS.pick(DyeColor.RED);
        #endif
    }

    public static @NotNull Item getWhiteStainedGlassItem() {
        #if MC_VER < MC_26_2
        return Items.WHITE_STAINED_GLASS;
        #elif MC_VER >= MC_26_2
        return Items.STAINED_GLASS.pick(DyeColor.WHITE);
        #endif
    }

    public static @NotNull Item getLimeStainedGlassItem() {
        #if MC_VER < MC_26_2
        return Items.LIME_STAINED_GLASS;
        #elif MC_VER >= MC_26_2
        return Items.STAINED_GLASS.pick(DyeColor.LIME);
        #endif
    }

    public static @NotNull Item getGreenBannerItem() {
        #if MC_VER < MC_26_2
        return Items.GREEN_BANNER;
        #elif MC_VER >= MC_26_2
        return Items.BANNER.pick(DyeColor.GREEN);
        #endif
    }

    public static @NotNull Item getRedBannerItem() {
        #if MC_VER < MC_26_2
        return Items.RED_BANNER;
        #elif MC_VER >= MC_26_2
        return Items.BANNER.pick(DyeColor.RED);
        #endif
    }

    public static @NotNull Item getGrayStainedGlassPaneItem() {
        #if MC_VER < MC_26_2
        return Items.GRAY_STAINED_GLASS_PANE;
        #elif MC_VER >= MC_26_2
        return Items.STAINED_GLASS_PANE.pick(DyeColor.GRAY);
        #endif
    }

    public static @NotNull Item getRedStainedGlassPaneItem() {
        #if MC_VER < MC_26_2
        return Items.RED_STAINED_GLASS_PANE;
        #elif MC_VER >= MC_26_2
        return Items.STAINED_GLASS_PANE.pick(DyeColor.RED);
        #endif
    }
}
