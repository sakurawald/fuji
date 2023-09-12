package fun.sakurawald.module.newbie_welcome;

import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.better_fake_player.BetterFakePlayerModule;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.server.level.ServerPlayer;

public class NewbieWelcomeModule {
    public static void welcomeNewbiePlayer(ServerPlayer player) {

        /* ignore carpet fake-player */
        if (BetterFakePlayerModule.isFakePlayer(player)) return;


        /* send welcome message */
        MessageUtil.broadcast(MessageUtil.resolve(ConfigManager.configWrapper.instance().modules.newbie_welcome.welcome_message, player), false);

        /* random teleport */
        RandomTeleport.randomTeleport(player, player.serverLevel(), true);
    }
}
