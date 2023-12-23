package io.github.sakurawald.module.initializer.more;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.item.ItemStack;


public class MoreModule extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("more").executes(this::$more));
    }

    @SuppressWarnings("SameReturnValue")
    private int $more(CommandContext<CommandSourceStack> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, (player -> {
            ItemStack mainHandItem = player.getMainHandItem();
            mainHandItem.setCount(mainHandItem.getMaxStackSize());
            return Command.SINGLE_SUCCESS;
        }));
    }

}
