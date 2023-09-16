package fun.sakurawald.module.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ConfigModule {

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("sw").requires(source -> source.hasPermission(4)).then(
                        Commands.literal("reload").executes(ConfigModule::$reload)
                )
        );
    }

    private static int $reload(CommandContext<CommandSourceStack> ctx) {
        ConfigManager.configWrapper.loadFromDisk();
        ConfigManager.chatWrapper.loadFromDisk();
        ConfigManager.pvpWrapper.loadFromDisk();
        MessageUtil.feedback(ctx.getSource(), "Reload successfully.");
        return Command.SINGLE_SUCCESS;
    }

}
