package io.github.sakurawald.module.initializer;


import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import javax.naming.OperationNotSupportedException;

public abstract class ModuleInitializer {

    public final void initialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
        this.onInitialize();
    }

    public void onInitialize() {
        // no-op
    }

    public void onReload() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("This module don't support reload");
    }

    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        // no-op
    }

}
