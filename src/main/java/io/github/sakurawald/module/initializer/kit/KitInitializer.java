package io.github.sakurawald.module.initializer.kit;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.kit.gui.KitEditorGui;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.NbtUtil;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@Slf4j
public class KitInitializer extends ModuleInitializer {

    public static final String INVENTORY = "inventory";
    private final Path STORAGE_PATH = Fuji.CONFIG_PATH.resolve("kit");

    public void writeKit(Kit kit) {
        Path path = STORAGE_PATH.resolve(kit.getName());
        NbtCompound root = NbtUtil.read(path);

        if (root == null) {
            Fuji.LOGGER.warn("failed to write kit {}", kit);
            return;
        }

        NbtList nbtList = new NbtList();
        NbtUtil.writeSlotsNode(nbtList, kit.getStackList());

        root.put(INVENTORY, nbtList);
        NbtUtil.write(root, path);
    }

    public List<String> getKitNameList() {
        List<String> ret = new ArrayList<>();
        try {
            Files.list(STORAGE_PATH).forEach(p -> ret.add(p.toFile().getName()));
        } catch (IOException e) {
            Fuji.LOGGER.error("failed to list kits {}", e.toString());
        }
        return ret;
    }

    public List<Kit> readKits() {
        List<Kit> ret = new ArrayList<>();

        for (String name : getKitNameList()) {
            ret.add(readKit(name));
        }

        return ret;
    }

    public @NotNull Kit readKit(String name) {
        Path p = STORAGE_PATH.resolve(name);
        NbtCompound root = NbtUtil.read(p);
        if (root == null) {
            return new Kit(p.toFile().getName(), new ArrayList<>());
        }

        NbtList nbtList = (NbtList) root.get(INVENTORY);
        List<ItemStack> itemStacks = NbtUtil.readSlotsNode(nbtList);
        return new Kit(p.toFile().getName(), itemStacks);
    }

    @Override
    public void onInitialize() {
        STORAGE_PATH.toFile().mkdirs();
    }

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("kit").requires(s -> s.hasPermissionLevel(4))
                        .then(literal("editor").executes(this::$editor))
                        .then(literal("give")
                                .then(argument("player", EntityArgumentType.player())
                                        .then(argument("name", StringArgumentType.greedyString())
                                                .suggests((context, builder) -> {
                                                    getKitNameList().forEach(builder::suggest);
                                                    return builder.buildFuture();
                                                })
                                                .executes(this::$give)))));
    }

    private int $editor(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            List<Kit> kits = readKits();
            new KitEditorGui(player, kits).open();
            return Command.SINGLE_SUCCESS;
        });
    }

   /*
   * - %fuji:check_counter <counter-name> <player>%
   * - kit give <player>
   * - %fuji:update_counter <counter-name> <player>%
   *
   * counter for: times, cooldown
   * */
    @SneakyThrows
    private int $give(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        String name = StringArgumentType.getString(ctx, "name");

        Kit kit = readKit(name);

        PlayerInventory playerInventory = player.getInventory();
        for (int i = 0; i < kit.getStackList().size(); i++) {
            ItemStack copy = kit.getStackList().get(i).copy();

            if (playerInventory.getStack(i).isEmpty()) {
                playerInventory.setStack(i, copy);
            } else {
                player.dropStack(copy);
            }
        }

        return 0;
    }

}
