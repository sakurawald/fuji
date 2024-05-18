package io.github.sakurawald.module.initializer.more;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;


public class MoreModule extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("more").executes(this::$more));
    }

    @SuppressWarnings("SameReturnValue")
    private int $more(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, (player -> {
            ItemStack mainHandItem = player.getMainHandStack();
            mainHandItem.setCount(mainHandItem.getMaxCount());
            return Command.SINGLE_SUCCESS;
        }));
    }

}
