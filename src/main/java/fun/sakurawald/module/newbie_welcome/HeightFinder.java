package fun.sakurawald.module.newbie_welcome;

import net.minecraft.world.chunk.Chunk;

import java.util.OptionalInt;

@FunctionalInterface
public interface HeightFinder {
    /**
     * Attempts to find a safe surface Y value for the specified X & Z values.
     *
     * @return A Y value corresponding to the player's feet pos
     */
    OptionalInt getY(Chunk chunk, int x, int z);
}

