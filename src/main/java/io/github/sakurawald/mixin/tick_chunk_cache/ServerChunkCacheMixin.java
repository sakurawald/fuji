package io.github.sakurawald.mixin.tick_chunk_cache;

import io.github.sakurawald.module.tick_chunk_cache.ITickableChunkSource;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;getChunks()Ljava/lang/Iterable;"))
    private Iterable<ChunkHolder> $tickChunks(ChunkMap instance) {
        return ((ITickableChunkSource) instance).sakurawald$tickableChunksIterator();
    }

}
