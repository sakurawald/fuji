package io.github.sakurawald.module.initializer.head.structure;

import io.github.sakurawald.module.initializer.head.HeadInitializer;
import io.github.sakurawald.auxiliary.minecraft.ItemHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum EconomyType {
    ITEM,
    FREE;

    public static void tryPurchase(@NotNull ServerPlayerEntity player, int amount, @NotNull Runnable onPurchase) {
        int trueAmount = amount * HeadInitializer.headHandler.model().costAmount;
        switch (HeadInitializer.headHandler.model().economyType) {
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

    public static Text getCost() {
        return switch (HeadInitializer.headHandler.model().economyType) {
            case ITEM ->
                    Text.empty().append(getCostItem().getName()).append(Text.of(" × " + HeadInitializer.headHandler.model().costAmount));
            case FREE -> Text.empty();
        };
    }

    private static @NotNull Item getCostItem() {
        return ItemHelper.ofItem(HeadInitializer.headHandler.model().costType);
    }
}
