package io.github.sakurawald.module.tick_chunk_cache;


import net.minecraft.server.level.ChunkHolder;

public interface ITickableChunkSource {

    Iterable<ChunkHolder> fuji$tickableChunksIterator();

}
