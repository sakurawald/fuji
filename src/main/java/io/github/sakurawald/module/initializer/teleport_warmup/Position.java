package io.github.sakurawald.module.initializer.teleport_warmup;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.util.MessageUtil;
import lombok.Data;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

@Data
public class Position {
    private String level;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Position(String level, double x, double y, double z, float yaw, float pitch) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Position(Level level, double x, double y, double z, float yaw, float pitch) {
        this(level.dimension().location().toString(), x, y, z, yaw, pitch);
    }

    public static Position of(ServerPlayer player) {
        return new Position(player.level().dimension().location().toString(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
    }

    public boolean sameLevel(Level level) {
        return this.level.equals(level.dimension().location().toString());
    }

    public double distanceToSqr(Position position) {
        if (!this.level.equals(position.level)) return Double.MAX_VALUE;
        double x = this.x - position.x;
        double y = this.y - position.y;
        double z = this.z - position.z;
        return x * x + y * y + z * z;
    }

    public void teleport(ServerPlayer player) {
        ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(this.level));
        ServerLevel serverLevel = Fuji.SERVER.getLevel(worldKey);
        if (serverLevel == null) {
            MessageUtil.sendMessage(player, "level.no_exists", this.level);
            return;
        }

        player.teleportTo(serverLevel, this.x, this.y, this.z, this.yaw, this.pitch);
    }
}
