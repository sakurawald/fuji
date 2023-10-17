package io.github.sakurawald.module.newbie_welcome.random_teleport;

import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.OptionalInt;

@FunctionalInterface
public interface HeightFinder {
    /**
     * Attempts to find a safe surface Y value for the specified X & Z values.
     *
     * @return A Y value corresponding to the player's feet pos
     */
    OptionalInt getY(ChunkAccess chunk, int x, int z);
}

