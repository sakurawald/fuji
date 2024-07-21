package io.github.sakurawald.module.initializer.command_toolbox.trashcan;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

public class TrashCanInitializer extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("trashcan").executes(this::$trashcan));
    }

    private int $trashcan(CommandContext<ServerCommandSource> ctx) {

        int rows = 3;
        SimpleInventory simpleInventory = new SimpleInventory(rows * 9);

        return CommandUtil.playerOnlyCommand(ctx, player -> {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, inventory, p) -> new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, i,inventory, simpleInventory, rows), MessageUtil.ofText(player, "trashcan.gui.title")));
            player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return Command.SINGLE_SUCCESS;
        });
    }
}
