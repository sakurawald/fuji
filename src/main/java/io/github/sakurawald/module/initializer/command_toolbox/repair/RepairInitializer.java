package io.github.sakurawald.module.initializer.command_toolbox.repair;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;


public class RepairInitializer extends ModuleInitializer {

    @Command("repair")
    private int $repair(@CommandSource ServerPlayerEntity player) {
        player.getMainHandStack().setDamage(0);
        MessageHelper.sendMessage(player, "repair");
        return CommandHelper.Return.SUCCESS;
    }

}
