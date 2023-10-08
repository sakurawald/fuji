package fun.sakurawald.mixin.tick_chunk_cache;

import fun.sakurawald.ServerMain;
import fun.sakurawald.mixin.MixinConfigPlugin;
import fun.sakurawald.module.tick_chunk_cache.ITickableChunkSource;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {

    @Unique
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> EMPTY_ITERABLE = new Long2ObjectLinkedOpenHashMap<>();

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;getChunks()Ljava/lang/Iterable;"))
    private Iterable<ChunkHolder> $tickChunks(ChunkMap instance) {
        if (ServerMain.SERVER.getTickCount() % MixinConfigPlugin.optimizationWrapper.instance().optimization.chunk.interval_ticks == 0) {
            return ((ITickableChunkSource) instance).sakurawald$tickableChunksIterator();
        }
        return EMPTY_ITERABLE.values();
    }

}
