package io.github.sakurawald.module.initializer.enderchest;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;


public class EnderChestModule extends ModuleInitializer {

    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("enderchest").executes(this::$enderchest));
    }

    @SuppressWarnings("SameReturnValue")
    private int $enderchest(CommandContext<CommandSourceStack> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            PlayerEnderChestContainer enderChestInventory = player.getEnderChestInventory();
            player.openMenu(new SimpleMenuProvider((i, inventory, p) -> ChestMenu.threeRows(i, inventory, enderChestInventory), Component.translatable("container.enderchest")));
            player.awardStat(Stats.OPEN_ENDERCHEST);
            return Command.SINGLE_SUCCESS;
        });
    }
}
