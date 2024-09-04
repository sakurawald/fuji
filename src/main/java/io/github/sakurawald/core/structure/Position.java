package io.github.sakurawald.core.structure;

import io.github.sakurawald.core.auxiliary.minecraft.IdentifierHelper;
import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import lombok.Data;
import lombok.With;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

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

    public Position(@NotNull World level, double x, double y, double z, float yaw, float pitch) {
        this(level.getRegistryKey().getValue().toString(), x, y, z, yaw, pitch);
    }

    public static @NotNull Position of(@NotNull ServerPlayerEntity player) {
        return new Position(player.getWorld().getRegistryKey().getValue().toString(), player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
    }

    public static @NotNull Position of(@NotNull ServerPlayerEntity player, @NotNull ServerWorld world) {
        BlockPos spawnPos = world.getSpawnPos();
        return new Position(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), player.getYaw(), player.getPitch());
    }

    public ServerWorld ofDimension() {
        return IdentifierHelper.ofServerWorld(Identifier.of(this.level));
    }

    public @NotNull BlockPos ofBlockPos() {
        return new BlockPos((int) this.x, (int) this.y, (int) this.z);
    }

    public boolean sameLevel(@NotNull World level) {
        return this.level.equals(level.getRegistryKey().getValue().toString());
    }

    public double distanceToSqr(@NotNull Position position) {
        if (!this.level.equals(position.level)) return Double.MAX_VALUE;
        double x = this.x - position.x;
        double y = this.y - position.y;
        double z = this.z - position.z;
        return x * x + y * y + z * z;
    }

    public void teleport(@NotNull ServerPlayerEntity player) {
        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(this.level));
        ServerWorld serverLevel = ServerHelper.getDefaultServer().getWorld(worldKey);
        if (serverLevel == null) {
            MessageHelper.sendMessage(player, "level.no_exists", this.level);
            return;
        }

        player.teleport(serverLevel, this.x, this.y, this.z, this.yaw, this.pitch);
    }
}
