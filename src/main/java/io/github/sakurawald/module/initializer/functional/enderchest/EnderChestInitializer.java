package io.github.sakurawald.module.initializer.functional.enderchest;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;


public class EnderChestInitializer extends ModuleInitializer {

    @Command("enderchest")
    private int $enderchest(@CommandSource ServerPlayerEntity player) {
        EnderChestInventory enderChestInventory = player.getEnderChestInventory();
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, inventory, p) -> GenericContainerScreenHandler.createGeneric9x3(i, inventory, enderChestInventory), Text.translatable("container.enderchest")));
        player.incrementStat(Stats.OPEN_ENDERCHEST);
        return CommandHelper.Return.SUCCESS;
    }
}
