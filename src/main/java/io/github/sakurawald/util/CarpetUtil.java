package io.github.sakurawald.util;

import lombok.experimental.UtilityClass;
import net.minecraft.server.network.ServerPlayerEntity;

@UtilityClass
public class CarpetUtil {
    public static boolean isFakePlayer(ServerPlayerEntity player) {
        return player.getClass() != ServerPlayerEntity.class;
    }
}
