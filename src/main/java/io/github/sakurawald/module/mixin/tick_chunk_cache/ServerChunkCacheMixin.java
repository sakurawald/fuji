package io.github.sakurawald.module.mixin.tick_chunk_cache;

import io.github.sakurawald.module.initializer.tick_chunk_cache.ITickableChunkSource;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerChunkManager.class)
public class ServerChunkCacheMixin {

//    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;entryIterator()Ljava/lang/Iterable;"))
//    private Iterable<ChunkHolder> $tickChunks(ThreadedAnvilChunkStorage instance) {
//        return ((ITickableChunkSource) instance).fuji$tickableChunksIterator();
//    }

}
