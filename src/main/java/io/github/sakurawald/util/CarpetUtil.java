package io.github.sakurawald.util;

import net.minecraft.server.level.ServerPlayer;

public class CarpetUtil {
    public static boolean isFakePlayer(ServerPlayer player) {
        return player.getClass() != ServerPlayer.class;
    }
}
