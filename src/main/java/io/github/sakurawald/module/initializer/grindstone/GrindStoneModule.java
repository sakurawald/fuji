package io.github.sakurawald.module.initializer.grindstone;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;

public class GrindStoneModule extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("grindstone").executes(this::$grindstone));
    }

    private int $grindstone(CommandContext<CommandSourceStack> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            player.openMenu(new SimpleMenuProvider((i, inventory, p) -> new GrindstoneMenu(i, inventory, ContainerLevelAccess.create(p.level(), p.blockPosition())) {
                @Override
                public boolean stillValid(Player player) {
                    return true;
                }
            }, Component.translatable("container.grindstone_title")));
            return Command.SINGLE_SUCCESS;
        });
    }
}
