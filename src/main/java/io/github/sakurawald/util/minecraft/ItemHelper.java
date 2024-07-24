package io.github.sakurawald.util.minecraft;

import lombok.experimental.UtilityClass;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@UtilityClass
public class ItemHelper {

    public static Item ofItem(String identifier) {
        return Registries.ITEM.get(Identifier.tryParse(identifier));
    }
}
