package fun.sakurawald.module.deathlog;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.ServerMain;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.commands.Commands.argument;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Slf4j
public class DeathLogModule {
    private static final Path STORAGE_PATH = ServerMain.CONFIG_PATH.resolve("deathlog");
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

    static {
        STORAGE_PATH.toFile().mkdirs();
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("deathlog").requires(s -> s.hasPermission(4))
                        .then(Commands.literal("view").then(argument("from", word()).executes(DeathLogModule::$view)))
                        .then(Commands.literal("restore")
                                .then(argument("from", word())
                                        .then(argument("index", IntegerArgumentType.integer())
                                                .then(argument("to", EntityArgument.player()).executes(DeathLogModule::$restore))))
                        ));
    }

    @SuppressWarnings("DataFlowIssue")
    @SneakyThrows
    private static int $restore(CommandContext<CommandSourceStack> ctx) {
        /* read from file */
        CommandSourceStack source = ctx.getSource();
        String from = StringArgumentType.getString(ctx, "from");
        int index = IntegerArgumentType.getInteger(ctx, "index");
        ServerPlayer to = EntityArgument.getPlayer(ctx, "to");

        File file = STORAGE_PATH.resolve(getStorageFileName(from)).toFile();
        CompoundTag rootTag;
        rootTag = NbtIo.read(file);
        if (rootTag == null) {
            source.sendMessage(Component.text("No deathlog found."));
            return 0;
        }

        ListTag deathsTag = (ListTag) rootTag.get(DEATHS);
        if (index >= deathsTag.size()) {
            source.sendMessage(Component.text("Index out of bound."));
            return 0;
        }
        CompoundTag deathTag = deathsTag.getCompound(index);
        CompoundTag inventoryTag = deathTag.getCompound(INVENTORY);
        List<ItemStack> item = readSlotsTag((ListTag) inventoryTag.get(ITEM));
        List<ItemStack> armor = readSlotsTag((ListTag) inventoryTag.get(ARMOR));
        List<ItemStack> offhand = readSlotsTag((ListTag) inventoryTag.get(OFFHAND));

        // check to player's inventory for safety
        if (!to.getInventory().isEmpty() && !ServerMain.SERVER.getPlayerList().isOp(to.getGameProfile())) {
            source.sendMessage(Component.text("To player's inventory is not empty!"));
            return Command.SINGLE_SUCCESS;
        }

        /* restore inventory */
        for (int i = 0; i < item.size(); i++) {
            to.getInventory().items.set(i, item.get(i));
        }
        for (int i = 0; i < armor.size(); i++) {
            to.getInventory().armor.set(i, armor.get(i));
        }
        for (int i = 0; i < offhand.size(); i++) {
            to.getInventory().offhand.set(i, offhand.get(i));
        }
        to.setScore(inventoryTag.getInt(SCORE));
        to.experienceLevel = inventoryTag.getInt(XP_LEVEL);
        to.experienceProgress = inventoryTag.getFloat(XP_PROGRESS);
        source.sendMessage(Component.text("Restore %s's death log %d for %s".formatted(from, index, to.getGameProfile().getName())));
        return Command.SINGLE_SUCCESS;
    }

    private static String getStorageFileName(String playerName) {
        return UUIDUtil.createOfflinePlayerUUID(playerName) + ".dat";
    }

    @SuppressWarnings("DataFlowIssue")
    @SneakyThrows
    private static int $view(CommandContext<CommandSourceStack> ctx) {
        String from = StringArgumentType.getString(ctx, "from");

        File file = STORAGE_PATH.resolve(getStorageFileName(from)).toFile();
        CompoundTag rootTag;
        rootTag = NbtIo.read(file);

        if (rootTag == null) {
            ctx.getSource().sendMessage(Component.text("No deathlog found."));
            return 0;
        }

        ListTag deaths = (ListTag) rootTag.get(DEATHS);
        TextComponent.Builder builder = Component.text();
        String to = Objects.requireNonNull(ctx.getSource().getPlayer()).getGameProfile().getName();
        for (int i = 0; i < deaths.size(); i++) {
            builder.append(asViewComponent(deaths.getCompound(i), from, i, to));
        }

        ctx.getSource().sendMessage(builder.asComponent());
        return Command.SINGLE_SUCCESS;
    }

    private static Component asViewComponent(CompoundTag deathTag, String from, int index, String to) {
        CompoundTag remarkTag = deathTag.getCompound(REMARK);
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
    public static void store(ServerPlayer player) {
        File file = new File(STORAGE_PATH.toString(), getStorageFileName(player.getGameProfile().getName()));

        CompoundTag rootTag;
        if (!file.exists()) {
            NbtIo.write(new CompoundTag(), file);
        }
        rootTag = NbtIo.read(file);
        if (rootTag == null) return;

        ListTag deathsTag;
        if (!rootTag.contains(DEATHS)) {
            rootTag.put(DEATHS, new ListTag());
        }
        deathsTag = (ListTag) rootTag.get(DEATHS);

        CompoundTag deathTag = new CompoundTag();
        writeInventoryTag(deathTag, player);
        writeRemarkTag(deathTag, player);
        deathsTag.add(deathTag);

        NbtIo.write(rootTag, file);
    }

    private static void writeRemarkTag(CompoundTag deathTag, ServerPlayer player) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String reason = player.getCombatTracker().getDeathMessage().getString();
        String dimension = player.level().dimension().location().toString();
        Vec3 position = player.position();

        CompoundTag remarkTag = new CompoundTag();
        remarkTag.putString(TIME, time);
        remarkTag.putString(REASON, reason);
        remarkTag.putString(DIMENSION, dimension);
        remarkTag.putDouble(X, position.x);
        remarkTag.putDouble(Y, position.y);
        remarkTag.putDouble(Z, position.z);
        deathTag.put(REMARK, remarkTag);
    }

    private static void writeInventoryTag(CompoundTag deathTag, ServerPlayer player) {
        Inventory inventory = player.getInventory();
        NonNullList<ItemStack> armor = inventory.armor;
        NonNullList<ItemStack> offhand = inventory.offhand;
        NonNullList<ItemStack> items = inventory.items;

        CompoundTag inventoryTag = new CompoundTag();
        inventoryTag.put(ARMOR, writeSlotsTag(new ListTag(), armor));
        inventoryTag.put(OFFHAND, writeSlotsTag(new ListTag(), offhand));
        inventoryTag.put(ITEM, writeSlotsTag(new ListTag(), items));
        inventoryTag.putInt(SCORE, player.getScore());
        inventoryTag.putInt(XP_LEVEL, player.experienceLevel);
        inventoryTag.putFloat(XP_PROGRESS, player.experienceProgress);

        deathTag.put(INVENTORY, inventoryTag);
    }

    private static ListTag writeSlotsTag(ListTag slotsTag, NonNullList<ItemStack> itemStackList) {
        for (ItemStack item : itemStackList) {
            slotsTag.add(item.save(new CompoundTag()));
        }
        return slotsTag;
    }

    private static List<ItemStack> readSlotsTag(ListTag slotsTag) {
        ArrayList<ItemStack> ret = new ArrayList<>();
        for (int i = 0; i < slotsTag.size(); i++) {
            ret.add(ItemStack.of(slotsTag.getCompound(i)));
        }
        return ret;
    }

}