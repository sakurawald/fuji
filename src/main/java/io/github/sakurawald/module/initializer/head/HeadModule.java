package io.github.sakurawald.module.initializer.head;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.head.api.Category;
import io.github.sakurawald.module.initializer.head.api.Head;
import io.github.sakurawald.module.initializer.head.api.HeadDatabaseAPI;
import io.github.sakurawald.module.initializer.head.gui.HeadGui;
import io.github.sakurawald.util.CommandUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.util.concurrent.CompletableFuture;

// Thanks to: https://modrinth.com/mod/headindex
public class HeadModule extends ModuleInitializer {

    public final HeadDatabaseAPI HEAD_DATABASE = new HeadDatabaseAPI();
    public Multimap<Category, Head> heads = HashMultimap.create();

    @SuppressWarnings("UnstableApiUsage")
    public void tryPurchase(ServerPlayerEntity player, int amount, Runnable onPurchase) {
        int trueAmount = amount * Configs.headHandler.model().costAmount;
        switch (Configs.headHandler.model().economyType) {
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
        return switch (Configs.headHandler.model().economyType) {
            case ITEM ->
                    Text.empty().append(getCostItem().getName()).append(Text.of(" × " + Configs.headHandler.model().costAmount));
            case FREE -> Text.empty();
        };
    }

    public Item getCostItem() {
        return Registries.ITEM.get(Identifier.tryParse(Configs.headHandler.model().costType));
    }

    @Override
    public void onInitialize() {
        CompletableFuture.runAsync(() -> heads = HEAD_DATABASE.getHeads());
        Configs.headHandler.loadFromDisk();
    }

    @Override
    public void onReload() {
        Configs.headHandler.loadFromDisk();
    }

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("head").executes(this::$head));
    }

    public int $head(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            new HeadGui(player).open();
            return Command.SINGLE_SUCCESS;
        });
    }

    public enum EconomyType {
        ITEM,
        FREE
    }


}
