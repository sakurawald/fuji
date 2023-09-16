package fun.sakurawald.module.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.config.ConfigManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static fun.sakurawald.util.MessageUtil.sendMessage;


public class ConfigModule {

    @SuppressWarnings("unused")
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
        sendMessage(ctx.getSource(), "reload");
        return Command.SINGLE_SUCCESS;
    }

}
