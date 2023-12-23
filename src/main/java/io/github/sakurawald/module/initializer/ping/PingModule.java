package io.github.sakurawald.module.initializer.ping;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;


public class PingModule extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("ping").executes(this::$ping)
                .then(argument("player", EntityArgument.player()).executes(this::$ping))
        );
    }

    @SuppressWarnings("SameReturnValue")
    private int $ping(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
            String name = target.getGameProfile().getName();
            int latency = target.connection.getPlayer().latency;
            MessageUtil.sendMessage(ctx.getSource(), "ping.player", name, latency);
        } catch (Exception e) {
            MessageUtil.sendMessage(ctx.getSource(), "ping.target.no_found");
        }

        return Command.SINGLE_SUCCESS;
    }

}
