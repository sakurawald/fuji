package io.github.sakurawald.module.initializer.command_toolbox.extinguish;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.network.ServerPlayerEntity;


public class ExtinguishInitializer extends ModuleInitializer {


    @CommandNode("extinguish")
    private static int $extinguish(@CommandSource ServerPlayerEntity player) {
        player.setFireTicks(0);
        return CommandHelper.Return.SUCCESS;
    }

}
