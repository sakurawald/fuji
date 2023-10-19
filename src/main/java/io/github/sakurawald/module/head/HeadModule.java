package io.github.sakurawald.module.head;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.module.head.api.Category;
import io.github.sakurawald.module.head.api.Head;
import io.github.sakurawald.module.head.api.HeadDatabaseAPI;
import io.github.sakurawald.module.head.gui.HeadGui;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

// Thanks to: https://modrinth.com/mod/headindex
public class HeadModule extends AbstractModule {

    public final HeadDatabaseAPI HEAD_DATABASE = new HeadDatabaseAPI();
    public Multimap<Category, Head> heads = HashMultimap.create();

    @SuppressWarnings("UnstableApiUsage")
    public void tryPurchase(ServerPlayer player, int amount, Runnable onPurchase) {
        int trueAmount = amount * ConfigManager.headWrapper.instance().costAmount;
        switch (ConfigManager.headWrapper.instance().economyType) {
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

    public Component getCost() {
        return switch (ConfigManager.headWrapper.instance().economyType) {
            case ITEM ->
                    Component.empty().append(getCostItem().getDescription()).append(Component.nullToEmpty(" × " + ConfigManager.headWrapper.instance().costAmount));
            case FREE -> Component.empty();
        };
    }

    public Item getCostItem() {
        return BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(ConfigManager.headWrapper.instance().costType));
    }

    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.configWrapper.instance().modules.head.enable;
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
        CompletableFuture.runAsync(() -> heads = HEAD_DATABASE.getHeads());
        ConfigManager.headWrapper.loadFromDisk();
    }

    @Override
    public void onReload() {
        ConfigManager.headWrapper.loadFromDisk();
    }

    @SuppressWarnings("unused")
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("head").executes(this::$head));
    }

    public int $head(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        new HeadGui(player).open();
        return Command.SINGLE_SUCCESS;
    }

    public enum EconomyType {
        ITEM,
        FREE
    }


}
