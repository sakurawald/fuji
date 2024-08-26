package io.github.sakurawald.module.initializer.command_toolbox.heal;

import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;


public class HealInitializer extends ModuleInitializer {


    @CommandNode("heal")
    private int $heal(@CommandSource ServerPlayerEntity player) {
        player.setHealth(player.getMaxHealth());
        MessageHelper.sendMessage(player, "heal");
        return CommandHelper.Return.SUCCESS;
    }

}
