package io.github.sakurawald.module.initializer.command_toolbox.trashcan;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.stat.Stats;

public class TrashCanInitializer extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("trashcan").executes(this::$trashcan));
    }

    private int $trashcan(CommandContext<ServerCommandSource> ctx) {

        int rows = 3;
        SimpleInventory simpleInventory = new SimpleInventory(rows * 9);

        return CommandHelper.playerOnlyCommand(ctx, player -> {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, inventory, p) -> new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, i,inventory, simpleInventory, rows), MessageHelper.ofText(player, "trashcan.gui.title")));
            player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return CommandHelper.Return.SUCCESS;
        });
    }
}
