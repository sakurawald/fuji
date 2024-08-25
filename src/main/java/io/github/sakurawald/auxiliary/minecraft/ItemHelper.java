package io.github.sakurawald.auxiliary.minecraft;

import lombok.experimental.UtilityClass;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class ItemHelper {

    public static @NotNull Item ofItem(@NotNull String identifier) {
        return Registries.ITEM.get(Identifier.tryParse(identifier));
    }
}
