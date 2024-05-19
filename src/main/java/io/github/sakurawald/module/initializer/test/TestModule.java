package io.github.sakurawald.module.initializer.test;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;


public class TestModule extends ModuleInitializer {

    @SneakyThrows
    private static int simulateLag(CommandContext<ServerCommandSource> ctx) {
        Fuji.SERVER.getCommandManager().getDispatcher().execute("execute in minecraft:overworld run test fake-players", ctx.getSource());
        Fuji.SERVER.getCommandManager().getDispatcher().execute("execute in minecraft:overworld run time set midnight", ctx.getSource());
        Fuji.SERVER.getCommandManager().getDispatcher().execute("execute in minecraft:the_nether run test fake-players", ctx.getSource());
        Fuji.SERVER.getCommandManager().getDispatcher().execute("execute in minecraft:the_end run test fake-players", ctx.getSource());

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings({"ConstantValue", "ReassignedVariable", "PointlessArithmeticExpression", "DataFlowIssue"})
    @SneakyThrows
    private static int fakePlayers(CommandContext<ServerCommandSource> ctx) {
        int amount = 25;
        int startIndex = 0;
        if (ctx.getSource().getWorld().getRegistryKey() == World.OVERWORLD) startIndex = amount * 0;
        if (ctx.getSource().getWorld().getRegistryKey() == World.NETHER) startIndex = amount * 1;
        if (ctx.getSource().getWorld().getRegistryKey() == World.END) startIndex = amount * 2;
        for (int i = 0; i < amount; i++) {
            int distance = i * 100;
            Fuji.SERVER.getCommandManager().getDispatcher().execute("player %d spawn at %d 96 %d".formatted(startIndex++, distance, distance), ctx.getSource());
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int clearChat(CommandContext<ServerCommandSource> ctx) {
        for (int i = 0; i < 50; i++) {
            ctx.getSource().sendMessage(Component.empty());
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int magic(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        player.sendMessage(Text.literal(String.valueOf(player.getMainHandStack().getComponents())));

        return 1;
    }


    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("test").requires(s -> s.hasPermissionLevel(4))
                        .then(CommandManager.literal("fake-players").executes(TestModule::fakePlayers))
                        .then(CommandManager.literal("simulate-lag").executes(TestModule::simulateLag))
                        .then(CommandManager.literal("clear-chat").executes(TestModule::clearChat))
                        .then(CommandManager.literal("magic").executes(TestModule::magic))
        );
    }
}
