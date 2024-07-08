package io.github.sakurawald.module.initializer.ping;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;


public class PingInitializer extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("ping").executes(this::$ping)
                .then(argument("player", EntityArgumentType.player()).executes(this::$ping))
        );
    }

    @SuppressWarnings("SameReturnValue")
    private int $ping(CommandContext<ServerCommandSource> ctx) {

        try {
            ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
            String name = target.getGameProfile().getName();
            int latency = target.networkHandler.getLatency();
            MessageUtil.sendMessage(ctx.getSource(), "ping.player", name, latency);
        } catch (Exception e) {
            MessageUtil.sendMessage(ctx.getSource(), "entity.no_found");
        }

        return Command.SINGLE_SUCCESS;
    }

}
