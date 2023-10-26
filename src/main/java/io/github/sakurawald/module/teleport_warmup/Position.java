package io.github.sakurawald.module.teleport_warmup;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

@Data
@AllArgsConstructor
public class Position {
    private Level level;
    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;

    public static Position of(ServerPlayer player) {
        return new Position(player.level(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
    }

    public double distanceToSqr(Position position) {
        if (this.level != position.level) return Double.MAX_VALUE;
        double x = this.x - position.x;
        double y = this.y - position.y;
        double z = this.z - position.z;
        return x * x + y * y + z * z;
    }
}
