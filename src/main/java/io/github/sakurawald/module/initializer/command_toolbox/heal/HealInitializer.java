package io.github.sakurawald.module.initializer.command_toolbox.heal;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;


public class HealInitializer extends ModuleInitializer {


    @CommandNode("heal")
    private static int $heal(@CommandSource ServerPlayerEntity player) {
        return $heal(player.getCommandSource(), player);
    }

    @CommandNode("heal")
    @CommandRequirement(level = 4)
    private static int $heal(@CommandSource ServerCommandSource source, ServerPlayerEntity target) {
        target.setHealth(target.getMaxHealth());
        LocaleHelper.sendMessageByKey(target, "heal");
        return CommandHelper.Return.SUCCESS;
    }

}
