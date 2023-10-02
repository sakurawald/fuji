package fun.sakurawald.module.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.AbstractModule;
import fun.sakurawald.module.ModuleManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static fun.sakurawald.util.MessageUtil.sendMessage;


public class ConfigModule extends AbstractModule {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
    }

    @SuppressWarnings("unused")
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("sw").requires(source -> source.hasPermission(4)).then(
                        Commands.literal("reload").executes(this::$reload)
                )
        );
    }

    private int $reload(CommandContext<CommandSourceStack> ctx) {
        // reload configs
        ConfigManager.loadConfigsFromDisk();
        // reload modules
        ModuleManager.reloadModules();
        sendMessage(ctx.getSource(), "reload");
        return Command.SINGLE_SUCCESS;
    }

}
