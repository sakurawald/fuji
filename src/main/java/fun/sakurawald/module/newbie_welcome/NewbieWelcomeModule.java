package fun.sakurawald.module.newbie_welcome;

import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.server.level.ServerPlayer;

public class NewbieWelcomeModule {
    public static void welcomeNewbiePlayer(ServerPlayer player) {
        /* welcome message */
        MessageUtil.broadcast(MessageUtil.resolve(ConfigManager.configWrapper.instance().modules.newbie_welcome.welcome_message, player), false);

        /* random teleport */
        RandomTeleport.randomTeleport(player, player.serverLevel(), true);
    }
}
