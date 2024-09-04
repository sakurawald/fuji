package io.github.sakurawald.module.initializer.command_toolbox.suicide;

import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import net.minecraft.server.network.ServerPlayerEntity;


public class SuicideInitializer extends ModuleInitializer {


    @CommandNode("suicide")
    private int $suicide(@CommandSource ServerPlayerEntity player) {
        player.kill();
        return CommandHelper.Return.SUCCESS;
    }

}
