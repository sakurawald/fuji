package io.github.sakurawald.module.initializer.command_toolbox.heal;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.network.ServerPlayerEntity;


public class HealInitializer extends ModuleInitializer {


    @CommandNode("heal")
    private static int $heal(@CommandSource ServerPlayerEntity player) {
        return $heal(player, player);
    }

    @CommandNode("heal")
    @CommandRequirement(level = 4)
    private static int $heal(@CommandSource ServerPlayerEntity player, ServerPlayerEntity target) {
        player.setHealth(player.getMaxHealth());
        LocaleHelper.sendMessageByKey(player, "heal");
        return CommandHelper.Return.SUCCESS;
    }

}
