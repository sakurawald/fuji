package io.github.sakurawald.module.initializer.carpet.better_info;

import com.mojang.brigadier.CommandDispatcher;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import java.util.List;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;


public class BetterInfoInitializer extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("info").then(
                        dispatcher.findNode(List.of("data", "get", "entity"))
                )
        );
    }
}
