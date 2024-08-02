package io.github.sakurawald.util.minecraft;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class EntityHelper {

    public static boolean isRealPlayer(@NotNull ServerPlayerEntity player) {
        return player.getClass() == ServerPlayerEntity.class;
    }

    public static boolean isNonRealPlayer(@NotNull ServerPlayerEntity player) {
        return !isRealPlayer(player);
    }

    public static BlockPos getBlockPosBelowEntity(@NotNull Entity entity) {
        if (entity.getY() % 1 != 0) {
            return entity.getBlockPos();

        }

        return entity.getBlockPos().add(0, -1,0);
    }

}
