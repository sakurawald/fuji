package fun.sakurawald.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;


public class HeadGSON {
    public EconomyType economyType = EconomyType.ITEM;
    public String costType = "minecraft:emerald_block";
    public int costAmount = 1;

    public Component getCost() {
        return switch (economyType) {
            case ITEM ->
                    Component.empty().append(getCostItem().getDescription()).append(Component.nullToEmpty(" × " + costAmount));
            case FREE -> Component.empty();
        };
    }

    public Item getCostItem() {
        return BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(costType));
    }

    public enum EconomyType {
        ITEM,
        FREE
    }

}
