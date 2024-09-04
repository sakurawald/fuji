package io.github.sakurawald.core.auxiliary.minecraft;

import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class IdentifierHelper {

    public static @NotNull String ofString(@NotNull ItemStack itemStack) {
        Item item = itemStack.getItem().asItem();
        return Registries.ITEM.getId(item).toString();
    }

    public static @NotNull String ofString(@NotNull BlockState blockState) {
        return ofString(blockState.getBlock());
    }

    public static @NotNull String ofString(Block block) {
        return Registries.BLOCK.getId(block).toString();
    }

    public static @NotNull String ofString(@NotNull Entity entity) {
        return Registries.ENTITY_TYPE.getId(entity.getType()).toString();
    }

    public static @NotNull String ofString(@NotNull ServerWorld serverWorld) {
        return serverWorld.getRegistryKey().getValue().toString();
    }

    public static <T> Registry<T> ofRegistry(RegistryKey<? extends Registry<? extends T>> registryKey) {
        return ServerHelper.getDefaultServer().getRegistryManager().get(registryKey);
    }

    public static <T> RegistryKey<T> ofRegistryKey(@NotNull RegistryKey<? extends Registry<T>> registryKey, Identifier identifier) {
        return RegistryKey.of(registryKey, identifier);
    }

    public static <T> RegistryEntry.@Nullable Reference<T> ofRegistryEntry(RegistryKey<? extends Registry<T>> registryKey, Identifier identifier) {
        return ofRegistry(registryKey).getEntry(identifier).orElse(null);
    }

    public static @Nullable ServerWorld ofServerWorld(Identifier identifier) {
        RegistryKey<World> key = ofRegistryKey(RegistryKeys.WORLD, identifier);
        return ServerHelper.getDefaultServer().getWorld(key);
    }

}
