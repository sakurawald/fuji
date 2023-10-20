package io.github.sakurawald.module.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.util.MessageUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.function.Supplier;


public class ConfigModule extends AbstractModule {

    @Override
    public Supplier<Boolean> enableModule() {
        return () -> true;
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
    }

    @SuppressWarnings("unused")
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("sakurawald").requires(source -> source.hasPermission(4)).then(
                        Commands.literal("reload").executes(this::$reload)
                )
        );
    }

    private int $reload(CommandContext<CommandSourceStack> ctx) {
        // reload modules
        ModuleManager.reloadModules();

        MessageUtil.sendMessage(ctx.getSource(), "reload");
        return Command.SINGLE_SUCCESS;
    }

}
