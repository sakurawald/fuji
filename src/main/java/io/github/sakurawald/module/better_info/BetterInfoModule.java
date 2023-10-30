package io.github.sakurawald.module.better_info;

import com.mojang.brigadier.CommandDispatcher;
import io.github.sakurawald.module.AbstractModule;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;


public class BetterInfoModule extends AbstractModule {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
    }

    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("info").then(
                        dispatcher.findNode(List.of("data", "get", "entity"))
                )
        );
    }
}
