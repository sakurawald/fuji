package mod.fuji.core.document.annotation;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD})
@Repeatable(value = ColorBoxes.class)
public @interface ColorBox {

    long id();

    ColorBoxTypes color();

    String value();

    enum ColorBoxTypes {

        EXAMPLE("colorbox.example.name", Icons.getLightGrayCandle())
        , TIP("colorbox.tips.name", Icons.getLimeCandle())
        , NOTE("colorbox.note.name", Icons.getBlueCandle())
        , WARNING("colorbox.warning.name", Icons.getYellowCandle())
        , DANGER("colorbox.danger.name", Icons.getRedCandle());

        final String languageKey;
        @SuppressWarnings("ImmutableEnumChecker")
        final Item item;

        ColorBoxTypes(String languageKey, Item item) {
            this.languageKey = languageKey;
            this.item = item;
        }

        public String toLanguageKey() {
            return this.languageKey;
        }

        public Item toItem() {
            return this.item;
        }

        private static class Icons {
            private static @NotNull Item getLightGrayCandle() {
            #if MC_VER < MC_26_2
            return Items.LIGHT_GRAY_CANDLE;
            #elif MC_VER >= MC_26_2
            return Items.DYED_CANDLE.pick(DyeColor.LIGHT_GRAY);
            #endif
            }

            private static @NotNull Item getLimeCandle() {
            #if MC_VER < MC_26_2
            return Items.LIME_CANDLE;
            #elif MC_VER >= MC_26_2
            return Items.DYED_CANDLE.pick(DyeColor.LIME);
            #endif
            }

            private static @NotNull Item getBlueCandle() {
            #if MC_VER < MC_26_2
            return Items.BLUE_CANDLE;
            #elif MC_VER >= MC_26_2
            return Items.DYED_CANDLE.pick(DyeColor.BLUE);
            #endif
            }

            private static @NotNull Item getYellowCandle() {
            #if MC_VER < MC_26_2
            return Items.YELLOW_CANDLE;
            #elif MC_VER >= MC_26_2
            return Items.DYED_CANDLE.pick(DyeColor.YELLOW);
            #endif
            }

            private static @NotNull Item getRedCandle() {
            #if MC_VER < MC_26_2
            return Items.RED_CANDLE
            #elif MC_VER >= MC_26_2
            return Items.DYED_CANDLE.pick(DyeColor.RED);
            #endif
            }
        }

    }
}
