package io.github.sakurawald.util;

import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

@UtilityClass
public class IdentifierUtil {

    public static String getItemStackIdentifier(ItemStack itemStack) {
        Item item = itemStack.getItem().asItem();
        return Registries.ITEM.getId(item).toString();
    }

    public static String getBlockStateIdentifier(BlockState blockState) {
        return getBlockIdentifier(blockState.getBlock());
    }

    public static String getBlockIdentifier(Block block) {
        return Registries.BLOCK.getId(block).toString();
    }
    public static String getEntityTypeIdentifier(Entity entity) {
        return Registries.ENTITY_TYPE.getId(entity.getType()).toString();
    }

}
