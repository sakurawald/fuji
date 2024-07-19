package io.github.sakurawald.util;

import lombok.experimental.UtilityClass;
import net.minecraft.server.network.ServerPlayerEntity;

@UtilityClass
public class EntityUtil {

    public static boolean isRealPlayer(ServerPlayerEntity player) {
        return player.getClass() == ServerPlayerEntity.class;
    }

    public static boolean isFakePlayer(ServerPlayerEntity player) {
        return !isRealPlayer(player);
    }
}
