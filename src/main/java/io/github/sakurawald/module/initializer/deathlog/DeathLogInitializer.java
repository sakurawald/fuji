package io.github.sakurawald.module.initializer.deathlog;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.command.argument.wrapper.OfflinePlayerName;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.NbtHelper;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Command("deathlog")
@CommandPermission(level = 4)
public class DeathLogInitializer extends ModuleInitializer {
    private final Path STORAGE_PATH = Fuji.CONFIG_PATH.resolve("deathlog");

    private final String DEATHS = "Deaths";
    private final String TIME = "time";
    private final String REASON = "reason";
    private final String DIMENSION = "dimension";
    private final String X = "x";
    private final String Y = "y";
    private final String Z = "z";
    private final String REMARK = "remark";
    private final String ARMOR = "armor";
    private final String OFFHAND = "offhand";
    private final String ITEM = "item";
    private final String SCORE = "score";
    private final String XP_LEVEL = "xp_level";
    private final String XP_PROGRESS = "xp_progress";
    private final String INVENTORY = "inventory";

    @Override
    public void onInitialize() {
        STORAGE_PATH.toFile().mkdirs();
    }

    @SneakyThrows
    @Command("restore")
    private int $restore(@CommandSource CommandContext<ServerCommandSource> ctx, String from, int index, ServerPlayerEntity to) {
        /* read from file */
        ServerCommandSource source = ctx.getSource();

        Path path = STORAGE_PATH.resolve(getFileName(from));
        NbtCompound root = NbtHelper.read(path);
        if (root == null || root.isEmpty()) {
            source.sendMessage(Component.text("No deathlog found."));
            return CommandHelper.Return.FAIL;
        }

        NbtList deathsNode = (NbtList) NbtHelper.getOrDefault(root, DEATHS, new NbtList());
        if (index >= deathsNode.size()) {
            source.sendMessage(Component.text("Index out of bound."));
            return CommandHelper.Return.FAIL;
        }

        // check the player's inventory for safety
        if (!to.getInventory().isEmpty()) {
            source.sendMessage(Component.text("To player's inventory is not empty!"));
            return CommandHelper.Return.FAIL;
        }

        /* restore inventory */
        NbtCompound inventoryNode = deathsNode.getCompound(index).getCompound(INVENTORY);
        List<ItemStack> item = NbtHelper.readSlotsNode((NbtList) inventoryNode.get(ITEM));
        for (int i = 0; i < item.size(); i++) {
            to.getInventory().main.set(i, item.get(i));
        }
        List<ItemStack> armor = NbtHelper.readSlotsNode((NbtList) inventoryNode.get(ARMOR));
        for (int i = 0; i < armor.size(); i++) {
            to.getInventory().armor.set(i, armor.get(i));
        }
        List<ItemStack> offhand = NbtHelper.readSlotsNode((NbtList) inventoryNode.get(OFFHAND));
        for (int i = 0; i < offhand.size(); i++) {
            to.getInventory().offHand.set(i, offhand.get(i));
        }
        to.setScore(inventoryNode.getInt(SCORE));
        to.experienceLevel = inventoryNode.getInt(XP_LEVEL);
        to.experienceProgress = inventoryNode.getFloat(XP_PROGRESS);

        source.sendMessage(Component.text("Restore %s's death log %d for %s".formatted(from, index, to.getGameProfile().getName())));
        return CommandHelper.Return.SUCCESS;
    }

    private @NotNull String getFileName(String playerName) {
        return Uuids.getOfflinePlayerUuid(playerName) + ".dat";
    }

    @Command("view")
    private int $view(@CommandSource ServerPlayerEntity player, OfflinePlayerName from) {
        String $from = from.getString();
        NbtCompound root = NbtHelper.read(STORAGE_PATH.resolve(getFileName($from)));
        if (root == null || root.isEmpty()) {
            player.sendMessage(Component.text("No deathlog found."));
            return CommandHelper.Return.FAIL;
        }

        NbtList deaths = (NbtList) NbtHelper.getOrDefault(root, DEATHS, new NbtList());
        TextComponent.Builder builder = Component.text();
        String to = player.getGameProfile().getName();
        for (int i = 0; i < deaths.size(); i++) {
            builder.append(asViewComponent(deaths.getCompound(i), $from, i, to));
        }

        player.sendMessage(builder.asComponent());
        return CommandHelper.Return.SUCCESS;
    }

    private @NotNull Component asViewComponent(@NotNull NbtCompound node, String from, int index, String to) {
        NbtCompound remarkTag = node.getCompound(REMARK);
        Component hover = Component.empty().color(NamedTextColor.DARK_GREEN)
                .append(Component.text("Time: " + remarkTag.getString(TIME)))
                .appendNewline()
                .append(Component.text("Reason: " + remarkTag.getString(REASON)))
                .appendNewline()
                .append(Component.text("Dimension: " + remarkTag.getString(DIMENSION)))
                .appendNewline()
                .append(Component.text("Coordinate: %f %f %f".formatted(
                        remarkTag.getDouble(X),
                        remarkTag.getDouble(Y),
                        remarkTag.getDouble(Z)
                )));
        return Component.empty()
                .color(NamedTextColor.RED)
                .append(Component.text(index))
                .appendSpace()
                .clickEvent(ClickEvent.runCommand("/deathlog restore %s %d %s".formatted(from, index, to)))
                .hoverEvent(HoverEvent.showText(hover));
    }

    public void store(@NotNull ServerPlayerEntity player) {
        Path path = STORAGE_PATH.resolve(getFileName(player.getGameProfile().getName()));

        NbtCompound root = NbtHelper.read(path);
        NbtList deathsNode = (NbtList) NbtHelper.getOrDefault(root, DEATHS, new NbtList());
        deathsNode.add(makeDeathNode(player));
        NbtHelper.write(root, path);
    }

    private @NotNull NbtCompound makeDeathNode(@NotNull ServerPlayerEntity player) {
        NbtCompound node = new NbtCompound();
        writeInventoryNode(node, player);
        writeRemarkNode(node, player);
        return node;
    }

    private void writeRemarkNode(@NotNull NbtCompound node, @NotNull ServerPlayerEntity player) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String reason = player.getDamageTracker().getDeathMessage().getString();
        String dimension = player.getWorld().getRegistryKey().getValue().toString();
        Vec3d position = player.getPos();

        NbtCompound remarkTag = new NbtCompound();
        remarkTag.putString(TIME, time);
        remarkTag.putString(REASON, reason);
        remarkTag.putString(DIMENSION, dimension);
        remarkTag.putDouble(X, position.x);
        remarkTag.putDouble(Y, position.y);
        remarkTag.putDouble(Z, position.z);
        node.put(REMARK, remarkTag);
    }

    private void writeInventoryNode(@NotNull NbtCompound node, @NotNull ServerPlayerEntity player) {
        NbtCompound inventoryTag = new NbtCompound();
        PlayerInventory inventory = player.getInventory();
        inventoryTag.put(ARMOR, NbtHelper.writeSlotsNode(new NbtList(), inventory.armor));
        inventoryTag.put(OFFHAND, NbtHelper.writeSlotsNode(new NbtList(), inventory.offHand));
        inventoryTag.put(ITEM, NbtHelper.writeSlotsNode(new NbtList(), inventory.main));
        inventoryTag.putInt(SCORE, player.getScore());
        inventoryTag.putInt(XP_LEVEL, player.experienceLevel);
        inventoryTag.putFloat(XP_PROGRESS, player.experienceProgress);
        node.put(INVENTORY, inventoryTag);
    }

}