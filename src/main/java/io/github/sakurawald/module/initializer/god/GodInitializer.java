package io.github.sakurawald.module.initializer.god;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;


public class GodInitializer extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("god").executes(this::$god));
    }

    @SuppressWarnings("SameReturnValue")
    private int $god(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            boolean flag = !player.getAbilities().invulnerable;
            player.getAbilities().invulnerable = flag;
            player.sendAbilitiesUpdate();

            MessageUtil.sendMessage(player, flag ? "god.on" : "god.off");
            return Command.SINGLE_SUCCESS;
        });
    }

}
