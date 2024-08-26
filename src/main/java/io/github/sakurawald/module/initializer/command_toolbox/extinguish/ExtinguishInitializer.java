package io.github.sakurawald.module.initializer.command_toolbox.extinguish;

import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import net.minecraft.server.network.ServerPlayerEntity;


public class ExtinguishInitializer extends ModuleInitializer {


    @CommandNode("extinguish")
    private int $extinguish(@CommandSource ServerPlayerEntity player) {
        player.setFireTicks(0);
        return CommandHelper.Return.SUCCESS;
    }

}
