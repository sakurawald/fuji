package fun.sakurawald.util;

import fun.sakurawald.ModMain;
import net.minecraft.server.level.ServerPlayer;

public class CarpetUtil {
    public static boolean isFakePlayer(ServerPlayer player) {
        return !ModMain.SERVER.getPlayerList().isWhiteListed(player.getGameProfile());
    }
}
