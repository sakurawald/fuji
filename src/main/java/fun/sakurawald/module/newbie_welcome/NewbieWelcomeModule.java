package fun.sakurawald.module.newbie_welcome;

import fun.sakurawald.ModMain;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.server.network.ServerPlayerEntity;

public class NewbieWelcomeModule {
    public static void welcomeNewbiePlayer(ServerPlayerEntity player) {
        /* ignore carpet fake-player */
        if (!ModMain.SERVER.getPlayerManager().isWhitelisted(player.getGameProfile())) {
            ModMain.LOGGER.info("NewbieWelcomeModule: " + player.getGameProfile() + " is not whitelisted, ignore it.");
            return;
        }

        /* send welcome message */
        MessageUtil.broadcast(MessageUtil.resolve(ConfigManager.configWrapper.instance().modules.newbie_welcome.welcome_message, player), false);

        /* random teleport */
        RandomTeleport.randomTeleport(player);
    }
}
