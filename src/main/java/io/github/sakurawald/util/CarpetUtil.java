package io.github.sakurawald.util;

import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerPlayer;

@UtilityClass
public class CarpetUtil {
    public static boolean isFakePlayer(ServerPlayer player) {
        return player.getClass() != ServerPlayer.class;
    }
}
