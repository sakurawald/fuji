package io.github.sakurawald.module.mixin.top_chunks;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkLoadingManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerChunkLoadingManager.class)
public interface ThreadedAnvilChunkStorageMixin {
    @Invoker("entryIterator")
    Iterable<ChunkHolder> $getChunks();
}
