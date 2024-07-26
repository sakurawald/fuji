package io.github.sakurawald.module.initializer.carpet.better_info;

import com.mojang.brigadier.CommandDispatcher;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import java.util.List;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.*;


public class BetterInfoInitializer extends ModuleInitializer {


    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(
                literal("info").then(
                        dispatcher.findNode(List.of("data", "get", "entity"))
                )
        );
    }
}
