package io.github.sakurawald.module.mixin.top_chunks;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class)
public interface ThreadedAnvilChunkStorageMixin {
    @Invoker("getChunks")
    Iterable<ChunkHolder> $getChunks();
}
