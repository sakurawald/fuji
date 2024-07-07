package io.github.sakurawald.module.initializer.feed;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;


public class FeedInitializer extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("feed").executes(this::$feed));
    }

    @SuppressWarnings("SameReturnValue")
    private int $feed(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            HungerManager foodData = player.getHungerManager();
            foodData.setFoodLevel(20);
            foodData.setSaturationLevel(5);
            foodData.setExhaustion(0);

            MessageUtil.sendMessage(player, "feed");
            return Command.SINGLE_SUCCESS;
        });
    }

}
