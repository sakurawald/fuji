package io.github.sakurawald.module.initializer.newbie_welcome;

import io.github.sakurawald.common.event.PostPlayerConnectEvent;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.newbie_welcome.random_teleport.RandomTeleport;
import io.github.sakurawald.util.CarpetUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;


public class NewbieWelcomeModule extends ModuleInitializer {
    @Override
    public void onInitialize() {
        PostPlayerConnectEvent.EVENT.register((connection, player, commonListenerCookie) -> {
            if (CarpetUtil.isFakePlayer(player)) return ActionResult.PASS;
            if (player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) < 1) {
                this.welcomeNewbiePlayer(player);
            }
            return ActionResult.PASS;
        });
    }

    public void welcomeNewbiePlayer(ServerPlayerEntity player) {
        /* welcome message */
        MessageUtil.sendBroadcast("newbie_welcome.welcome_message", player.getGameProfile().getName());

        /* random teleport */
        RandomTeleport.randomTeleport(player, player.getServerWorld(), true);
    }

}
