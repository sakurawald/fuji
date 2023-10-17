package io.github.sakurawald.module.newbie_welcome;

import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;


public class NewbieWelcomeModule extends AbstractModule {

    public void welcomeNewbiePlayer(ServerPlayer player) {
        /* welcome message */
        MessageUtil.sendBroadcast("newbie_welcome.welcome_message", player.getGameProfile().getName());

        /* random teleport */
        RandomTeleport.randomTeleport(player, player.serverLevel(), true);
    }

    @Override
    public Supplier<Boolean> enableModule() {
        return () -> true;
    }
}
