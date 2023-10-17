package io.github.sakurawald.mixin.tick_chunk_cache;

import io.github.sakurawald.module.tick_chunk_cache.ITickableChunkSource;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public class ChunkMapMixin implements ITickableChunkSource {

    @Shadow
    @Final
    private Long2ObjectLinkedOpenHashMap<ChunkHolder> updatingChunkMap;

    @Unique
    private Long2ObjectLinkedOpenHashMap<ChunkHolder> tickingChunksCache = new Long2ObjectLinkedOpenHashMap<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.tickingChunksCache = new Long2ObjectLinkedOpenHashMap<>();
    }

    @Inject(method = "onFullChunkStatusChange", at = @At("HEAD"))
    private void $onFullChunkStatusChange(ChunkPos chunkPos, FullChunkStatus levelType, CallbackInfo ci) {
        final ChunkHolder chunkHolder = this.updatingChunkMap.get(chunkPos.toLong());
        if (chunkHolder == null) return;
        if (chunkHolder.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING)) {
            this.tickingChunksCache.put(chunkPos.toLong(), chunkHolder);
        } else {
            this.tickingChunksCache.remove(chunkPos.toLong());
        }
    }

    @Override
    public Iterable<ChunkHolder> sakurawald$tickableChunksIterator() {
        return this.tickingChunksCache.values();
    }
}
