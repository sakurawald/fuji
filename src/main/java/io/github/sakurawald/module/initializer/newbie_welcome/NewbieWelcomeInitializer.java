package io.github.sakurawald.module.initializer.newbie_welcome;

import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.common.random_teleport.RandomTeleport;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.server.network.ServerPlayerEntity;


public class NewbieWelcomeInitializer extends ModuleInitializer {

    public void welcomeNewbiePlayer(ServerPlayerEntity player) {
        /* welcome message */
        MessageUtil.sendBroadcast("newbie_welcome.welcome_message", player.getGameProfile().getName());

        /* random teleport */
        RandomTeleport.randomTeleport(player, player.getServerWorld(), true);
    }

}
