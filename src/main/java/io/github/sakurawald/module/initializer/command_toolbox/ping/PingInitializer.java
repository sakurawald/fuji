package io.github.sakurawald.module.initializer.command_toolbox.ping;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import lombok.SneakyThrows;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import static net.minecraft.server.command.CommandManager.literal;


public class PingInitializer extends ModuleInitializer {

    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(literal("ping").then(CommandHelper.Argument.player().executes(this::$ping))
        );
    }

    @SneakyThrows
    private int $ping(@NotNull CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity target = CommandHelper.Argument.player(ctx);
        String name = target.getGameProfile().getName();

        int latency = target.networkHandler.getLatency();
        MessageHelper.sendMessage(ctx.getSource(), "ping.player", name, latency);

        return CommandHelper.Return.SUCCESS;
    }

}
