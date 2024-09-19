package io.github.sakurawald.module.initializer.kit;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.NbtHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.kit.command.argument.wrapper.KitName;
import io.github.sakurawald.module.initializer.kit.gui.KitEditorGui;
import io.github.sakurawald.module.initializer.kit.structure.Kit;
import lombok.SneakyThrows;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@CommandNode("kit")
@CommandRequirement(level = 4)
public class KitInitializer extends ModuleInitializer {

    public static final String INVENTORY = "inventory";

    private final Path STORAGE_PATH = ReflectionUtil.getModuleConfigPath(KitInitializer.class).resolve("kit-data");

    public void writeKit(@NotNull Kit kit) {
        Path path = STORAGE_PATH.resolve(kit.getName());

        NbtCompound root = NbtHelper.read(path);
        if (root == null) {
            LogUtil.warn("failed to write kit {}", kit);
            return;
        }

        NbtList nbtList = new NbtList();
        NbtHelper.writeSlotsNode(nbtList, kit.getStackList());

        root.put(INVENTORY, nbtList);
        NbtHelper.write(root, path);
    }

    public @NotNull List<String> getKitNameList() {
        List<String> ret = new ArrayList<>();
        try {
            Files.list(STORAGE_PATH).forEach(p -> ret.add(p.toFile().getName()));
        } catch (IOException e) {
            LogUtil.error("failed to list kits {}", e.toString());
        }
        return ret;
    }

    public @NotNull List<Kit> readKits() {
        List<Kit> ret = new ArrayList<>();
        for (String name : getKitNameList()) {
            ret.add(readKit(name));
        }
        return ret;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void deleteKit(@NotNull String name) {
        Path path = STORAGE_PATH.resolve(name);
        path.toFile().delete();
    }

    public @NotNull Kit readKit(@NotNull String name) {
        Path p = STORAGE_PATH.resolve(name);
        NbtCompound root = NbtHelper.read(p);

        if (root == null) {
            return new Kit(p.toFile().getName(), new ArrayList<>());
        }

        NbtList nbtList = (NbtList) root.get(INVENTORY);
        List<ItemStack> itemStacks = NbtHelper.readSlotsNode(nbtList);
        return new Kit(p.toFile().getName(), itemStacks);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onInitialize() {
        STORAGE_PATH.toFile().mkdirs();
    }

    @CommandNode("editor")
    private int $editor(@CommandSource ServerPlayerEntity player) {
        List<Kit> kits = readKits();
        new KitEditorGui(player, kits, 0).open();
        return CommandHelper.Return.SUCCESS;
    }

    /*
     * - %fuji:check_counter <counter-name> <player>%
     * - kit give <player>
     * - %fuji:update_counter <counter-name> <player>%
     *
     * counter for: times, cooldown
     * */
    @SneakyThrows
    @CommandNode("give")
    private int $give(@CommandSource CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player, KitName kit) {

        Kit $kit = readKit(kit.getValue());
        if ($kit.getStackList().isEmpty()) {
            LocaleHelper.sendMessageByKey(ctx.getSource(), "kit.kit.empty");
            return CommandHelper.Return.FAIL;
        }

        PlayerInventory playerInventory = player.getInventory();
        for (int i = 0; i < $kit.getStackList().size(); i++) {
            ItemStack copy = $kit.getStackList().get(i).copy();

            if (playerInventory.getStack(i).isEmpty()) {
                playerInventory.setStack(i, copy);
            } else {
                player.dropStack(copy);
            }
        }

        return CommandHelper.Return.SUCCESS;
    }

}
