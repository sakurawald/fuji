package fun.sakurawald.module.test;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.ServerMain;
import fun.sakurawald.module.AbstractModule;
import fun.sakurawald.module.ModuleManager;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class TestModule extends AbstractModule {
    private static int fakePlayerCount = 0;

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            CommandRegistrationCallback.EVENT.register(this::registerCommand);
        }
    }

    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("test")
                        .then(Commands.literal("fake-players").executes(TestModule::fakePlayers))
                        .then(Commands.literal("simulate-lag").executes(TestModule::simulateLag))
        );
    }

    @SneakyThrows
    private static int simulateLag(CommandContext<CommandSourceStack> ctx) {
        ServerMain.SERVER.getCommands().getDispatcher().execute("execute in minecraft:overworld run test fake-players", ctx.getSource());
        ServerMain.SERVER.getCommands().getDispatcher().execute("execute in minecraft:the_nether run test fake-players", ctx.getSource());
        ServerMain.SERVER.getCommands().getDispatcher().execute("execute in minecraft:the_end run test fake-players", ctx.getSource());

        return Command.SINGLE_SUCCESS;
    }

    @SneakyThrows
    private static int fakePlayers(CommandContext<CommandSourceStack> ctx) {
        for (int i = 0; i < 25; i++) {
            int id = fakePlayerCount++;
            int distance = fakePlayerCount * 100;
            ServerMain.SERVER.getCommands().getDispatcher().execute("player %d spawn at %d 64 %d".formatted(id, distance, distance) , ctx.getSource());
        }
        return Command.SINGLE_SUCCESS;
    }
}
