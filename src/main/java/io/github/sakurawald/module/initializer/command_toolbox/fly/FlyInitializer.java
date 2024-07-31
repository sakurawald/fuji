package io.github.sakurawald.module.initializer.command_toolbox.fly;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;


public class FlyInitializer extends ModuleInitializer {

    @Command("fly")
    private int $fly(@CommandSource ServerPlayerEntity player) {
        boolean flag = !player.getAbilities().allowFlying;
        player.getAbilities().allowFlying = flag;

        if (!flag) {
            player.getAbilities().flying = false;
        }

        player.sendAbilitiesUpdate();
        MessageHelper.sendMessage(player, flag ? "fly.on" : "fly.off");
        return CommandHelper.Return.SUCCESS;
    }
}
