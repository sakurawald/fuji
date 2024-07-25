package io.github.sakurawald.util.minecraft;

import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@UtilityClass
public class IdentifierHelper {

    public static String ofString(ItemStack itemStack) {
        Item item = itemStack.getItem().asItem();
        return Registries.ITEM.getId(item).toString();
    }

    public static String ofString(BlockState blockState) {
        return ofString(blockState.getBlock());
    }

    public static String ofString(Block block) {
        return Registries.BLOCK.getId(block).toString();
    }

    public static String ofString(Entity entity) {
        return Registries.ENTITY_TYPE.getId(entity.getType()).toString();
    }

    public static String ofString(ServerWorld serverWorld) {
        return serverWorld.getRegistryKey().getValue().toString();
    }

    public static RegistryKey<World> ofRegistryKey(Identifier identifier) {
        return RegistryKey.of(RegistryKeys.WORLD, identifier);
    }

    public static ServerWorld ofServerWorld(Identifier identifier) {
        RegistryKey<World> key = ofRegistryKey(identifier);
        return ServerHelper.getDefaultServer().getWorld(key);
    }

}
