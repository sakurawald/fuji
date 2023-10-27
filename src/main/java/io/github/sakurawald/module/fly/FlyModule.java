package io.github.sakurawald.module.fly;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

@Slf4j
public class FlyModule extends AbstractModule {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
    }

    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("fly").executes(this::$fly));
    }

    @SuppressWarnings("SameReturnValue")
    private int $fly(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        boolean flag = !player.getAbilities().mayfly;
        player.getAbilities().mayfly = flag;
        player.onUpdateAbilities();

        if (!flag) {
            player.getAbilities().flying = false;
        }

        MessageUtil.sendMessage(player, flag ? "fly.on" : "fly.off");
        return Command.SINGLE_SUCCESS;
    }

}
