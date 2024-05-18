package io.github.sakurawald.module.initializer.tick_chunk_cache;


import net.minecraft.server.world.ChunkHolder;

public interface ITickableChunkSource {

    Iterable<ChunkHolder> fuji$tickableChunksIterator();

}
