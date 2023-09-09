package fun.sakurawald.util;

import fun.sakurawald.ModMain;
import net.minecraft.server.network.ServerPlayerEntity;

public class CarpetUtil {
    public static boolean isFakePlayer(ServerPlayerEntity player) {
        return !ModMain.SERVER.getPlayerManager().isWhitelisted(player.getGameProfile());
    }
}
