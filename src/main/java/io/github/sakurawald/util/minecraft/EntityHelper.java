package io.github.sakurawald.util.minecraft;

import lombok.experimental.UtilityClass;
import net.minecraft.server.network.ServerPlayerEntity;

@UtilityClass
public class EntityHelper {

    public static boolean isRealPlayer(ServerPlayerEntity player) {
        return player.getClass() == ServerPlayerEntity.class;
    }

    public static boolean isNonRealPlayer(ServerPlayerEntity player) {
        return !isRealPlayer(player);
    }

}
