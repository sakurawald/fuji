package io.github.sakurawald.module.initializer.biome_lookup_cache;

import com.mojang.datafixers.util.Either;
import net.minecraft.server.world.OptionalChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

/**
 * Utility methods for getting chunks.
 *
 * @author Wesley1808
 */
public class ChunkManager {

    @NotNull
    public static RegistryEntry<Biome> getRoughBiome(World level, BlockPos pos) {
        Chunk chunk = getChunkNow(level, pos);
        int x = pos.getX() >> 2;
        int y = pos.getY() >> 2;
        int z = pos.getZ() >> 2;

        return chunk != null ? chunk.getBiomeForNoiseGen(x, y, z) : level.getGeneratorStoredBiome(x, y, z);
    }

    @Nullable
    public static Chunk getChunkNow(WorldView levelReader, BlockPos pos) {
        return getChunkNow(levelReader, pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Nullable
    public static Chunk getChunkNow(WorldView levelReader, int chunkX, int chunkZ) {
        if (levelReader instanceof ServerWorld level) {
            return getChunkFromHolder(getChunkHolder(level, chunkX, chunkZ));
        } else {
            return levelReader.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
        }
    }

    @SuppressWarnings("ObjectEquality")
    @Nullable
    public static WorldChunk getChunkFromFuture(CompletableFuture<OptionalChunk<WorldChunk>> future) {

        WorldChunk now = future.getNow(null).orElse(null);

        if (now == ChunkHolder.UNLOADED_WORLD_CHUNK || now == null) {
            return null;
        }

        return now;
    }

    @Nullable
    public static WorldChunk getChunkFromHolder(ChunkHolder holder) {
        return holder != null ? getChunkFromFuture(holder.getAccessibleFuture()) : null;
    }

    @Nullable
    private static ChunkHolder getChunkHolder(ServerWorld level, int chunkX, int chunkZ) {
        return level.getChunkManager().getChunkHolder(ChunkPos.toLong(chunkX, chunkZ));
    }
}