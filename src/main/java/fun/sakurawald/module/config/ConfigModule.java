package fun.sakurawald.module.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ConfigModule {

    public static LiteralCommandNode<CommandSourceStack> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        return dispatcher.register(
                Commands.literal("sw").requires(source -> source.hasPermission(4)).then(
                        Commands.literal("reload").executes(ConfigModule::reload)
                )
        );
    }

    private static int reload(CommandContext<CommandSourceStack> ctx) {
        ConfigManager.configWrapper.loadFromDisk();
        MessageUtil.feedback(ctx.getSource(), "Reload successfully.");
        return 1;
    }

}
