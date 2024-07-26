package io.github.sakurawald.util.minecraft;

import io.github.sakurawald.util.LogUtil;
import lombok.experimental.UtilityClass;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class NbtHelper {

    public static void set(@NotNull NbtCompound root, @NotNull String path, NbtElement value) {
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

    public static NbtElement getOrDefault(@NotNull NbtCompound root, @NotNull String path, NbtElement defaultValue) {
        if (get(root, path) == null) {
            set(root, path, defaultValue);
        }

        return get(root, path);
    }

    public static @org.jetbrains.annotations.Nullable NbtElement get(@NotNull NbtCompound root, @NotNull String path) {
        // search the path
        String[] nodes = path.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            String node = nodes[i];

            if (!root.contains(node)) {
                LogUtil.error("Nbt {} don't has path {}", root, path);
            }

            root = root.getCompound(node);
        }

        // get the value
        String key = nodes[nodes.length - 1];
        return root.get(key);
    }

    public static @Nullable NbtCompound read(@NotNull Path path) {
        try {
            if (!path.toFile().exists()) {
                NbtIo.write(new NbtCompound(), path);
            }
            return NbtIo.read(path);
        } catch (IOException e) {
            LogUtil.error("failed to create nbt file in {}", path);
        }

        return null;
    }

    public static void write(@NotNull NbtCompound root, @NotNull Path path) {
        try {
            NbtIo.write(root, path);
        } catch (IOException e) {
            LogUtil.error("failed to write nbt file in {}", path);
        }
    }

    public static NbtList writeSlotsNode(@NotNull NbtList node, @NotNull List<ItemStack> itemStackList) {
        for (ItemStack item : itemStackList) {
            node.add(item.encodeAllowEmpty(RegistryHelper.getDefaultWrapperLookup()));
        }
        return node;
    }

    public static @NotNull List<ItemStack> readSlotsNode(@Nullable NbtList node) {
        if (node == null) return new ArrayList<>();

        ArrayList<ItemStack> ret = new ArrayList<>();
        for (int i = 0; i < node.size(); i++) {
            ret.add(ItemStack.fromNbtOrEmpty(RegistryHelper.getDefaultWrapperLookup(), node.getCompound(i)));
        }
        return ret;
    }
}
