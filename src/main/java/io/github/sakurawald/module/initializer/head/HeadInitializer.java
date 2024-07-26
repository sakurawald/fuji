package io.github.sakurawald.module.initializer.head;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.HeadModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.head.api.Category;
import io.github.sakurawald.module.initializer.head.api.Head;
import io.github.sakurawald.module.initializer.head.api.HeadDatabaseAPI;
import io.github.sakurawald.module.initializer.head.gui.HeadGui;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.ItemHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

// Thanks to: https://modrinth.com/mod/headindex
public class HeadInitializer extends ModuleInitializer {

    public static final ConfigHandler<HeadModel> headHandler = new ObjectConfigHandler<>("head.json", HeadModel.class);
    public final HeadDatabaseAPI HEAD_DATABASE = new HeadDatabaseAPI();
    public @NotNull Multimap<Category, Head> heads = HashMultimap.create();

    public void tryPurchase(@NotNull ServerPlayerEntity player, int amount, @NotNull Runnable onPurchase) {
        int trueAmount = amount * headHandler.model().costAmount;
        switch (headHandler.model().economyType) {
            case FREE -> onPurchase.run();
            case ITEM -> {
                try (Transaction transaction = Transaction.openOuter()) {
                    long extracted = PlayerInventoryStorage.of(player).extract(ItemVariant.of(getCostItem()), trueAmount, transaction);
                    if (extracted == trueAmount) {
                        transaction.commit();
                        onPurchase.run();
                    }
                }
            }
        }
    }

    public Text getCost() {
        return switch (headHandler.model().economyType) {
            case ITEM ->
                    Text.empty().append(getCostItem().getName()).append(Text.of(" × " + headHandler.model().costAmount));
            case FREE -> Text.empty();
        };
    }

    public @NotNull Item getCostItem() {
        return ItemHelper.ofItem(headHandler.model().costType);
    }

    @Override
    public void onInitialize() {
        CompletableFuture.runAsync(() -> heads = HEAD_DATABASE.getHeads());
        headHandler.loadFromDisk();
    }

    @Override
    public void onReload() {
        headHandler.loadFromDisk();
    }

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("head").executes(this::$head));
    }

    public int $head(@NotNull CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, player -> {
            new HeadGui(player).open();
            return CommandHelper.Return.SUCCESS;
        });
    }

    public enum EconomyType {
        ITEM,
        FREE
    }

}
