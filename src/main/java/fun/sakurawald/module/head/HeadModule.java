package fun.sakurawald.module.head;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.AbstractModule;
import fun.sakurawald.module.head.api.Category;
import fun.sakurawald.module.head.api.Head;
import fun.sakurawald.module.head.api.HeadDatabaseAPI;
import fun.sakurawald.module.head.gui.HeadGui;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

public class HeadModule extends AbstractModule {

    public static final HeadDatabaseAPI HEAD_DATABASE = new HeadDatabaseAPI();
    public static Multimap<Category, Head> heads = HashMultimap.create();

    @SuppressWarnings("UnstableApiUsage")
    public static void tryPurchase(ServerPlayer player, int amount, Runnable onPurchase) {
        int trueAmount = amount * ConfigManager.headWrapper.instance().costAmount;
        switch (ConfigManager.headWrapper.instance().economyType) {
            case FREE -> onPurchase.run();
            case ITEM -> {
                try (Transaction transaction = Transaction.openOuter()) {
                    long extracted = PlayerInventoryStorage.of(player).extract(ItemVariant.of(ConfigManager.headWrapper.instance().getCostItem()), trueAmount, transaction);
                    if (extracted == trueAmount) {
                        transaction.commit();
                        onPurchase.run();
                    }
                }
            }
        }
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
        CompletableFuture.runAsync(() -> heads = HEAD_DATABASE.getHeads());
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
}
