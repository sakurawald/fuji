package io.github.sakurawald.module.initializer;


import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Method;

public abstract class ModuleInitializer {

    public void initialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
        this.onInitialize();
    }

    public void onInitialize() {
        // no-op
    }

    public void onReload() {
        // no-op
    }



    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        // no-op
    }

}
