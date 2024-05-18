package io.github.sakurawald.module.mixin.tick_chunk_cache;

import io.github.sakurawald.module.initializer.tick_chunk_cache.ITickableChunkSource;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ChunkMapMixin implements ITickableChunkSource {

    @Shadow
    @Final
    private Long2ObjectLinkedOpenHashMap<ChunkHolder> currentChunkHolders;

    @Unique
    private Long2ObjectLinkedOpenHashMap<ChunkHolder> tickingChunksCache = new Long2ObjectLinkedOpenHashMap<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.tickingChunksCache = new Long2ObjectLinkedOpenHashMap<>();
    }

    @Inject(method = "onChunkStatusChange", at = @At("HEAD"))
    private void $onChunkStatusChange(ChunkPos chunkPos, ChunkLevelType levelType, CallbackInfo ci) {
        final ChunkHolder chunkHolder = this.currentChunkHolders.get(chunkPos.toLong());
        if (chunkHolder == null) return;
        if (chunkHolder.getLevelType().isAfter(ChunkLevelType.BLOCK_TICKING)) {
            this.tickingChunksCache.put(chunkPos.toLong(), chunkHolder);
        } else {
            this.tickingChunksCache.remove(chunkPos.toLong());
        }
    }

    @Override
    public Iterable<ChunkHolder> fuji$tickableChunksIterator() {
        return this.tickingChunksCache.values();
    }
}
