package io.github.sakurawald.module.initializer.better_info;

import com.mojang.brigadier.CommandDispatcher;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;


public class BetterInfoModule extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("info").then(
                        dispatcher.findNode(List.of("data", "get", "entity"))
                )
        );
    }
}
