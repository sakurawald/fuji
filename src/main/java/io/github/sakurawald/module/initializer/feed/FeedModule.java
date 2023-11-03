package io.github.sakurawald.module.initializer.feed;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;


public class FeedModule extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("feed").executes(this::$feed));
    }

    @SuppressWarnings("SameReturnValue")
    private int $feed(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        FoodData foodData = player.getFoodData();
        foodData.setFoodLevel(20);
        foodData.setSaturation(5);
        foodData.setExhaustion(0);

        MessageUtil.sendMessage(player, "feed");
        return Command.SINGLE_SUCCESS;
    }

}
