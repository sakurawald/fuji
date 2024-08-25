package io.github.sakurawald.module.initializer.command_toolbox.feed;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.server.network.ServerPlayerEntity;


public class FeedInitializer extends ModuleInitializer {

    @Command("feed")
    private int $feed(@CommandSource ServerPlayerEntity player) {
        HungerManager foodData = player.getHungerManager();
        foodData.setFoodLevel(20);
        foodData.setSaturationLevel(5);
        foodData.setExhaustion(0);

        MessageHelper.sendMessage(player, "feed");
        return CommandHelper.Return.SUCCESS;
    }

}
