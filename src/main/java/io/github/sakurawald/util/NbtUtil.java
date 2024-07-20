package io.github.sakurawald.util;

import io.github.sakurawald.Fuji;
import lombok.experimental.UtilityClass;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class NbtUtil {


    public static void set(NbtCompound root, String path, NbtElement value) {
        // search the path
        String[] nodes = path.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            String node = nodes[i];

            if (!root.contains(node)) {
                root.put(node, new NbtCompound());
            }

            root = root.getCompound(node);
        }

        // set the value
        String key = nodes[nodes.length - 1];
        root.put(key, value);
    }

    public static NbtElement getOrDefault(NbtCompound root, String path, NbtElement defaultValue) {
        if (get(root, path) == null) {
            set(root, path, defaultValue);
        }

        return get(root, path);
    }


    public static NbtElement get(NbtCompound root, String path) {
        // search the path
        String[] nodes = path.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            String node = nodes[i];

            if (!root.contains(node)) {
                Fuji.LOGGER.error("Nbt {} don't has path {}", root, path);
            }

            root = root.getCompound(node);
        }

        // get the value
        String key = nodes[nodes.length - 1];
        return root.get(key);
    }

    public static @Nullable NbtCompound read(Path path) {
        try {
            if (!path.toFile().exists()) {
                NbtIo.write(new NbtCompound(), path);
            }
            return NbtIo.read(path);
        } catch (IOException e) {
            Fuji.LOGGER.error("failed to create nbt file in {}", path);
        }

        return null;
    }

    public static void write(NbtCompound root, Path path) {
        try {
            NbtIo.write(root, path);
        } catch (IOException e) {
            Fuji.LOGGER.error("failed to write nbt file in {}", path);
        }
    }

    public static NbtList writeSlotsNode(NbtList node, List<ItemStack> itemStackList) {
        for (ItemStack item : itemStackList) {
            node.add(item.encodeAllowEmpty(RegistryUtil.getDefaultWrapperLookup()));
        }
        return node;
    }

    public static @NotNull List<ItemStack> readSlotsNode(@Nullable NbtList node) {
        if (node == null) return new ArrayList<>();

        ArrayList<ItemStack> ret = new ArrayList<>();
        for (int i = 0; i < node.size(); i++) {
            ret.add(ItemStack.fromNbtOrEmpty(RegistryUtil.getDefaultWrapperLookup(), node.getCompound(i)));
        }
        return ret;
    }
}
