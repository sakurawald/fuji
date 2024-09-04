package io.github.sakurawald.module.initializer.command_toolbox.repair;

import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;


public class RepairInitializer extends ModuleInitializer {

    @CommandNode("repair")
    private int $repair(@CommandSource ServerPlayerEntity player) {
        player.getMainHandStack().setDamage(0);
        MessageHelper.sendMessage(player, "repair");
        return CommandHelper.Return.SUCCESS;
    }

}
