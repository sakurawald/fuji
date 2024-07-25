package io.github.sakurawald.module.initializer.command_toolbox.feed;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
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
        return CommandHelper.Pattern.playerOnlyCommand(ctx, player -> {
            HungerManager foodData = player.getHungerManager();
            foodData.setFoodLevel(20);
            foodData.setSaturationLevel(5);
            foodData.setExhaustion(0);

            MessageHelper.sendMessage(player, "feed");
            return CommandHelper.Return.SUCCESS;
        });
    }

}
