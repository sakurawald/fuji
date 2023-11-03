package io.github.sakurawald.module.initializer.heal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;


public class HealModule extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("heal").executes(this::$heal));
    }

    @SuppressWarnings("SameReturnValue")
    private int $heal(CommandContext<CommandSourceStack> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            player.setHealth(player.getMaxHealth());
            MessageUtil.sendMessage(player, "heal");
            return Command.SINGLE_SUCCESS;
        });
    }

}
