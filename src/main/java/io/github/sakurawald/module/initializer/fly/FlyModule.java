package io.github.sakurawald.module.initializer.fly;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;


public class FlyModule extends ModuleInitializer {

    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("fly").executes(this::$fly));
    }

    private int $fly(CommandContext<CommandSourceStack> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, (player) -> {
            boolean flag = !player.getAbilities().mayfly;
            player.getAbilities().mayfly = flag;

            if (!flag) {
                player.getAbilities().flying = false;
            }

            player.onUpdateAbilities();
            MessageUtil.sendMessage(player, flag ? "fly.on" : "fly.off");
            return Command.SINGLE_SUCCESS;
        });
    }
}
