package io.github.sakurawald.module.initializer.enderchest;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;


public class EnderChestModule extends ModuleInitializer {

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("enderchest").executes(this::$enderchest));
    }

    @SuppressWarnings("SameReturnValue")
    private int $enderchest(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            EnderChestInventory enderChestInventory = player.getEnderChestInventory();
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, inventory, p) -> GenericContainerScreenHandler.createGeneric9x3(i, inventory, enderChestInventory), Text.translatable("container.enderchest")));
            player.incrementStat(Stats.OPEN_ENDERCHEST);
            return Command.SINGLE_SUCCESS;
        });
    }
}
