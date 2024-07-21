package io.github.sakurawald.util;

import lombok.experimental.UtilityClass;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@UtilityClass
public class ItemUtil {

    public static Item getItem(String identifier) {
        return Registries.ITEM.get(Identifier.tryParse(identifier));
    }
}
