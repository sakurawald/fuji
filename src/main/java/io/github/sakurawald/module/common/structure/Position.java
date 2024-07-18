package io.github.sakurawald.module.common.structure;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.util.MessageUtil;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Data
@With
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

    public Position(World level, double x, double y, double z, float yaw, float pitch) {
        this(level.getRegistryKey().getValue().toString(), x, y, z, yaw, pitch);
    }

    public static Position of(ServerPlayerEntity player) {
        return new Position(player.getWorld().getRegistryKey().getValue().toString(), player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
    }

    public static Position of(ServerPlayerEntity player, ServerWorld world) {
        BlockPos spawnPos = world.getSpawnPos();
        return new Position(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), player.getYaw(), player.getPitch());
    }

    public boolean sameLevel(World level) {
        return this.level.equals(level.getRegistryKey().getValue().toString());
    }

    public double distanceToSqr(Position position) {
        if (!this.level.equals(position.level)) return Double.MAX_VALUE;
        double x = this.x - position.x;
        double y = this.y - position.y;
        double z = this.z - position.z;
        return x * x + y * y + z * z;
    }

    public void teleport(ServerPlayerEntity player) {
        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(this.level));
        ServerWorld serverLevel = Fuji.SERVER.getWorld(worldKey);
        if (serverLevel == null) {
            MessageUtil.sendMessage(player, "level.no_exists", this.level);
            return;
        }

        player.teleport(serverLevel, this.x, this.y, this.z, this.yaw, this.pitch);
    }
}
