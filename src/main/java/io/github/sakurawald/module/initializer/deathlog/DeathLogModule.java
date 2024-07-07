package io.github.sakurawald.module.initializer.deathlog;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.RegistryUtil;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;

@SuppressWarnings("ResultOfMethodCallIgnored")

public class DeathLogModule extends ModuleInitializer {
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

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("deathlog").requires(s -> s.hasPermissionLevel(4))
                        .then(CommandManager.literal("view").then(CommandUtil.offlinePlayerArgument("from").executes(this::$view)))
                        .then(CommandManager.literal("restore")
                                .then(CommandUtil.offlinePlayerArgument("from")
                                        .then(argument("index", IntegerArgumentType.integer())
                                                .then(argument("to", EntityArgumentType.player()).executes(this::$restore))))
                        ));
    }

    @SuppressWarnings("DataFlowIssue")
    @SneakyThrows
    private int $restore(CommandContext<ServerCommandSource> ctx) {
        /* read from file */
        ServerCommandSource source = ctx.getSource();
        String from = StringArgumentType.getString(ctx, "from");
        int index = IntegerArgumentType.getInteger(ctx, "index");
        ServerPlayerEntity to = EntityArgumentType.getPlayer(ctx, "to");

        File file = STORAGE_PATH.resolve(getStorageFileName(from)).toFile();
        NbtCompound rootTag;
        rootTag = NbtIo.read(file.toPath());
        if (rootTag == null) {
            source.sendMessage(Component.text("No deathlog found."));
            return 0;
        }

        NbtList deathsTag = (NbtList) rootTag.get(DEATHS);
        if (index >= deathsTag.size()) {
            source.sendMessage(Component.text("Index out of bound."));
            return 0;
        }
        NbtCompound deathTag = deathsTag.getCompound(index);
        NbtCompound inventoryTag = deathTag.getCompound(INVENTORY);
        List<ItemStack> item = readSlotsTag((NbtList) inventoryTag.get(ITEM));
        List<ItemStack> armor = readSlotsTag((NbtList) inventoryTag.get(ARMOR));
        List<ItemStack> offhand = readSlotsTag((NbtList) inventoryTag.get(OFFHAND));

        // check the player's inventory for safety
        if (!to.getInventory().isEmpty() && !Fuji.SERVER.getPlayerManager().isOperator(to.getGameProfile())) {
            source.sendMessage(Component.text("To player's inventory is not empty!"));
            return Command.SINGLE_SUCCESS;
        }

        /* restore inventory */
        for (int i = 0; i < item.size(); i++) {
            to.getInventory().main.set(i, item.get(i));
        }
        for (int i = 0; i < armor.size(); i++) {
            to.getInventory().armor.set(i, armor.get(i));
        }
        for (int i = 0; i < offhand.size(); i++) {
            to.getInventory().offHand.set(i, offhand.get(i));
        }
        to.setScore(inventoryTag.getInt(SCORE));
        to.experienceLevel = inventoryTag.getInt(XP_LEVEL);
        to.experienceProgress = inventoryTag.getFloat(XP_PROGRESS);
        source.sendMessage(Component.text("Restore %s's death log %d for %s".formatted(from, index, to.getGameProfile().getName())));
        return Command.SINGLE_SUCCESS;
    }

    private String getStorageFileName(String playerName) {
        return Uuids.getOfflinePlayerUuid(playerName) + ".dat";
    }

    @SuppressWarnings("DataFlowIssue")
    @SneakyThrows
    private int $view(CommandContext<ServerCommandSource> ctx) {
        String from = StringArgumentType.getString(ctx, "from");

        File file = STORAGE_PATH.resolve(getStorageFileName(from)).toFile();
        NbtCompound rootTag;
        rootTag = NbtIo.read(file.toPath());

        if (rootTag == null) {
            ctx.getSource().sendMessage(Component.text("No deathlog found."));
            return 0;
        }

        NbtList deaths = (NbtList) rootTag.get(DEATHS);
        TextComponent.Builder builder = Component.text();
        String to = Objects.requireNonNull(ctx.getSource().getPlayer()).getGameProfile().getName();
        for (int i = 0; i < deaths.size(); i++) {
            builder.append(asViewComponent(deaths.getCompound(i), from, i, to));
        }

        ctx.getSource().sendMessage(builder.asComponent());
        return Command.SINGLE_SUCCESS;
    }

    private Component asViewComponent(NbtCompound deathTag, String from, int index, String to) {
        NbtCompound remarkTag = deathTag.getCompound(REMARK);
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
        return Component.empty().color(NamedTextColor.RED)
                .append(Component.text(index)).appendSpace()
                .clickEvent(ClickEvent.runCommand("/deathlog restore %s %d %s".formatted(from, index, to)))
                .hoverEvent(HoverEvent.showText(hover));
    }


    @SuppressWarnings("DataFlowIssue")
    @SneakyThrows
    public void store(ServerPlayerEntity player) {
        File file = new File(STORAGE_PATH.toString(), getStorageFileName(player.getGameProfile().getName()));

        NbtCompound rootTag;
        if (!file.exists()) {
            NbtIo.write(new NbtCompound(), file.toPath());
        }
        rootTag = NbtIo.read(file.toPath());
        if (rootTag == null) return;

        NbtList deathsTag;
        if (!rootTag.contains(DEATHS)) {
            rootTag.put(DEATHS, new NbtList());
        }
        deathsTag = (NbtList) rootTag.get(DEATHS);

        NbtCompound deathTag = new NbtCompound();
        writeInventoryTag(deathTag, player);
        writeRemarkTag(deathTag, player);
        deathsTag.add(deathTag);

        NbtIo.write(rootTag, file.toPath());
    }

    private void writeRemarkTag(NbtCompound deathTag, ServerPlayerEntity player) {
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
        deathTag.put(REMARK, remarkTag);
    }

    private void writeInventoryTag(NbtCompound deathTag, ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        DefaultedList<ItemStack> armor = inventory.armor;
        DefaultedList<ItemStack> offhand = inventory.offHand;
        DefaultedList<ItemStack> items = inventory.main;

        NbtCompound inventoryTag = new NbtCompound();
        inventoryTag.put(ARMOR, writeSlotsTag(new NbtList(), armor));
        inventoryTag.put(OFFHAND, writeSlotsTag(new NbtList(), offhand));
        inventoryTag.put(ITEM, writeSlotsTag(new NbtList(), items));
        inventoryTag.putInt(SCORE, player.getScore());
        inventoryTag.putInt(XP_LEVEL, player.experienceLevel);
        inventoryTag.putFloat(XP_PROGRESS, player.experienceProgress);

        deathTag.put(INVENTORY, inventoryTag);
    }

    private NbtList writeSlotsTag(NbtList slotsTag, DefaultedList<ItemStack> itemStackList) {
        for (ItemStack item : itemStackList) {
            slotsTag.add(item.encodeAllowEmpty(RegistryUtil.getDefaultWrapperLookup()));
        }
        return slotsTag;
    }

    private List<ItemStack> readSlotsTag(NbtList slotsTag) {
        ArrayList<ItemStack> ret = new ArrayList<>();
        for (int i = 0; i < slotsTag.size(); i++) {
            ret.add(ItemStack.fromNbtOrEmpty(RegistryUtil.getDefaultWrapperLookup(), slotsTag.getCompound(i)));
        }
        return ret;
    }

}