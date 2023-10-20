package io.github.sakurawald.module.tick_chunk_cache;

import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;

import java.util.function.Supplier;

public class TickChunkCacheModule extends AbstractModule {
    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.optimizationWrapper.instance().optimization.chunk.fastTickChunk;
    }
}
