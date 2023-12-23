package io.github.sakurawald.module.initializer.god;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;


public class GodModule extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("god").executes(this::$god));
    }

    @SuppressWarnings("SameReturnValue")
    private int $god(CommandContext<CommandSourceStack> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            boolean flag = !player.getAbilities().invulnerable;
            player.getAbilities().invulnerable = flag;
            player.onUpdateAbilities();

            MessageUtil.sendMessage(player, flag ? "god.on" : "god.off");
            return Command.SINGLE_SUCCESS;
        });
    }

}
