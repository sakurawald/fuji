package io.github.sakurawald.module.initializer.head.structure;

import io.github.sakurawald.core.auxiliary.minecraft.RegistryHelper;
import io.github.sakurawald.module.initializer.head.HeadInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public enum EconomyType {
    ITEM,
    FREE;

    @SuppressWarnings("WhileLoopReplaceableByForEach")
    private static boolean extract(ServerPlayerEntity player, @NotNull Item item, int amount) {
        Iterator<DefaultedList<ItemStack>> iterator = player.getInventory().combinedInventory.iterator();
        while (iterator.hasNext()) {
            DefaultedList<ItemStack> list = iterator.next();

            for (ItemStack itemStack : list) {
                if (itemStack.getItem().equals(item)
                    && !itemStack.hasEnchantments()
                    && itemStack.getCount() >= amount
                ) {
                    itemStack.decrement(amount);
                    return true;
                }
            }

        }
        return false;
    }

    public static void tryPurchase(@NotNull ServerPlayerEntity player, int amount, @NotNull Runnable onPurchase) {
        int trueAmount = amount * HeadInitializer.headHandler.getModel().cost_amount;
        switch (HeadInitializer.headHandler.getModel().economy_type) {
            case FREE -> onPurchase.run();
            case ITEM -> {
                if (extract(player, getCostItem(), trueAmount)) {
                    onPurchase.run();
                }
            }
        }
    }

    public static Text getCost() {
        return switch (HeadInitializer.headHandler.getModel().economy_type) {
            case ITEM ->
                Text.empty().append(getCostItem().getName()).append(Text.of(" × " + HeadInitializer.headHandler.getModel().cost_amount));
            case FREE -> Text.empty();
        };
    }

    private static @NotNull Item getCostItem() {
        return RegistryHelper.ofItem(HeadInitializer.headHandler.getModel().cost_type);
    }
}
