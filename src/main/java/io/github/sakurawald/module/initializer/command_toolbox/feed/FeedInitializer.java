package io.github.sakurawald.module.initializer.command_toolbox.feed;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.server.network.ServerPlayerEntity;


public class FeedInitializer extends ModuleInitializer {

    @CommandNode("feed")
    private static int $feed(@CommandSource ServerPlayerEntity player) {
        return $feed(player, player);
    }

    @CommandNode("feed")
    @CommandRequirement(level = 4)
    private static int $feed(@CommandSource ServerPlayerEntity player, ServerPlayerEntity target) {
        HungerManager foodData = player.getHungerManager();
        foodData.setFoodLevel(20);
        foodData.setSaturationLevel(5);
        foodData.setExhaustion(0);

        LocaleHelper.sendMessageByKey(player, "feed");
        return CommandHelper.Return.SUCCESS;
    }
}
