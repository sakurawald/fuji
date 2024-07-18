package io.github.sakurawald.module.initializer.command_toolbox.fly;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;


public class FlyInitializer extends ModuleInitializer {

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("fly").executes(this::$fly));
    }

    private int $fly(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, (player) -> {
            boolean flag = !player.getAbilities().allowFlying;
            player.getAbilities().allowFlying = flag;

            if (!flag) {
                player.getAbilities().flying = false;
            }

            player.sendAbilitiesUpdate();
            MessageUtil.sendMessage(player, flag ? "fly.on" : "fly.off");
            return Command.SINGLE_SUCCESS;
        });
    }
}
