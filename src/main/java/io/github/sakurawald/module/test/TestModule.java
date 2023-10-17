package io.github.sakurawald.module.test;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.ServerMain;
import io.github.sakurawald.module.AbstractModule;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class TestModule extends AbstractModule {

    @SneakyThrows
    private static int simulateLag(CommandContext<CommandSourceStack> ctx) {
        ServerMain.SERVER.getCommands().getDispatcher().execute("execute in minecraft:overworld run test fake-players", ctx.getSource());
        ServerMain.SERVER.getCommands().getDispatcher().execute("execute in minecraft:overworld run time set midnight", ctx.getSource());
        ServerMain.SERVER.getCommands().getDispatcher().execute("execute in minecraft:the_nether run test fake-players", ctx.getSource());
        ServerMain.SERVER.getCommands().getDispatcher().execute("execute in minecraft:the_end run test fake-players", ctx.getSource());

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings({"ConstantValue", "ReassignedVariable", "PointlessArithmeticExpression", "DataFlowIssue"})
    @SneakyThrows
    private static int fakePlayers(CommandContext<CommandSourceStack> ctx) {
        int amount = 25;
        int startIndex = 0;
        if (ctx.getSource().getLevel().dimension() == Level.OVERWORLD) startIndex = amount * 0;
        if (ctx.getSource().getLevel().dimension() == Level.NETHER) startIndex = amount * 1;
        if (ctx.getSource().getLevel().dimension() == Level.END) startIndex = amount * 2;
        for (int i = 0; i < amount; i++) {
            int distance = i * 100;
            ServerMain.SERVER.getCommands().getDispatcher().execute("player %d spawn at %d 96 %d".formatted(startIndex++, distance, distance), ctx.getSource());
        }
        return Command.SINGLE_SUCCESS;
    }


    @Override
    public Supplier<Boolean> enableModule() {
        return () -> FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
    }

    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("test").requires(s -> s.hasPermission(4))
                        .then(Commands.literal("fake-players").executes(TestModule::fakePlayers))
                        .then(Commands.literal("simulate-lag").executes(TestModule::simulateLag))
        );
    }
}
