package io.github.sakurawald.module.initializer.deathlog;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.NbtHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.OfflinePlayerName;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.SneakyThrows;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CommandNode("deathlog")
@CommandRequirement(level = 4)
public class DeathLogInitializer extends ModuleInitializer {
    private static final Path STORAGE_PATH = ReflectionUtil.getModuleConfigPath(DeathLogInitializer.class).resolve("death-data");

    private static final String DEATHS = "Deaths";
    private static final String TIME = "time";
    private static final String REASON = "reason";
    private static final String DIMENSION = "dimension";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";
    private static final String REMARK = "remark";
    private static final String ARMOR = "armor";
    private static final String OFFHAND = "offhand";
    private static final String ITEM = "item";
    private static final String SCORE = "score";
    private static final String XP_LEVEL = "xp_level";
    private static final String XP_PROGRESS = "xp_progress";
    private static final String INVENTORY = "inventory";

    @SneakyThrows(IOException.class)
    @Override
    public void onInitialize() {
        Files.createDirectories(STORAGE_PATH);
    }

    @CommandNode("restore")
    private static int $restore(@CommandSource CommandContext<ServerCommandSource> ctx, String from, int index, ServerPlayerEntity to) {
        /* read from file */
        ServerCommandSource source = ctx.getSource();

        Path path = STORAGE_PATH.resolve(getFileName(from));
        NbtCompound root = NbtHelper.readOrDefault(path);
        if (root == null || root.isEmpty()) {
            LocaleHelper.sendMessageByKey(ctx.getSource(), "deathlog.empty");
            return CommandHelper.Return.FAIL;
        }

        NbtList deathsNode = (NbtList) NbtHelper.getOrDefault(root, DEATHS, new NbtList());
        if (index >= deathsNode.size()) {
            LocaleHelper.sendMessageByKey(source, "deathlog.index.not_found", index);
            return CommandHelper.Return.FAIL;
        }

        // check the player's inventory for safety
        if (!to.getInventory().isEmpty()) {
            LocaleHelper.sendMessageByKey(source, "deathlog.restore.target_player.inventory_not_empty", to.getGameProfile().getName());
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

        LocaleHelper.sendMessageByKey(source, "deathlog.restore.success", from, index, to.getGameProfile().getName());
        return CommandHelper.Return.SUCCESS;
    }

    private static @NotNull String getFileName(String playerName) {
        return Uuids.getOfflinePlayerUuid(playerName) + ".dat";
    }

    @CommandNode("view")
    private static int $view(@CommandSource ServerPlayerEntity player, OfflinePlayerName from) {
        String $from = from.getValue();
        NbtCompound root = NbtHelper.readOrDefault(STORAGE_PATH.resolve(getFileName($from)));
        if (root == null || root.isEmpty()) {
            LocaleHelper.sendMessageByKey(player, "deathlog.empty");
            return CommandHelper.Return.FAIL;
        }

        NbtList deaths = (NbtList) NbtHelper.getOrDefault(root, DEATHS, new NbtList());

        MutableText deathlogViewText = Text.empty();
        String to = player.getGameProfile().getName();
        for (int i = 0; i < deaths.size(); i++) {
            deathlogViewText.append(asViewText(player, deaths.getCompound(i), $from, i, to));
        }

        player.sendMessage(deathlogViewText);
        return CommandHelper.Return.SUCCESS;
    }

    private static @NotNull Text asViewText(Object audience, @NotNull NbtCompound node, String from, int index, String to) {
        NbtCompound remarkTag = node.getCompound(REMARK);

        MutableText hoverText = Text.empty()
            .append(LocaleHelper.getTextByKey(audience, "deathlog.view.time", remarkTag.getString(TIME)))
            .append(LocaleHelper.TEXT_NEWLINE)
            .append(LocaleHelper.getTextByKey(audience, "deathlog.view.reason", remarkTag.getString(REASON)))
            .append(LocaleHelper.TEXT_NEWLINE)
            .append(LocaleHelper.getTextByKey(audience, "deathlog.view.dimension", remarkTag.getString(DIMENSION)))
            .append(LocaleHelper.TEXT_NEWLINE)
            .append(LocaleHelper.getTextByKey(audience, "deathlog.view.coordinate", remarkTag.getDouble(X), remarkTag.getDouble(Y), remarkTag.getDouble(Z)));

        return Text
            .literal(String.valueOf(index))
            .fillStyle(Style.EMPTY
                .withFormatting(Formatting.RED)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,hoverText))
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/deathlog restore %s %d %s".formatted(from, index, to)))
            )
            .append(LocaleHelper.TEXT_SPACE);
    }

    public static void store(@NotNull ServerPlayerEntity player) {
        Path path = STORAGE_PATH.resolve(getFileName(player.getGameProfile().getName()));

        NbtCompound root = NbtHelper.readOrDefault(path);
        NbtList deathsNode = (NbtList) NbtHelper.getOrDefault(root, DEATHS, new NbtList());
        deathsNode.add(makeDeathNode(player));
        NbtHelper.write(root, path);
    }

    private static @NotNull NbtCompound makeDeathNode(@NotNull ServerPlayerEntity player) {
        NbtCompound node = new NbtCompound();
        writeInventoryNode(node, player);
        writeRemarkNode(node, player);
        return node;
    }

    private static void writeRemarkNode(@NotNull NbtCompound node, @NotNull ServerPlayerEntity player) {
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

    private static void writeInventoryNode(@NotNull NbtCompound node, @NotNull ServerPlayerEntity player) {
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
