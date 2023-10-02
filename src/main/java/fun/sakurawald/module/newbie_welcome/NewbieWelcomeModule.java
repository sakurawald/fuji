package fun.sakurawald.module.newbie_welcome;

import fun.sakurawald.module.AbstractModule;
import net.minecraft.server.level.ServerPlayer;

import static fun.sakurawald.util.MessageUtil.sendBroadcast;


public class NewbieWelcomeModule extends AbstractModule {

    public void welcomeNewbiePlayer(ServerPlayer player) {
        /* welcome message */
        sendBroadcast("newbie_welcome.welcome_message", player.getGameProfile().getName());

        /* random teleport */
        RandomTeleport.randomTeleport(player, player.serverLevel(), true);
    }
}
