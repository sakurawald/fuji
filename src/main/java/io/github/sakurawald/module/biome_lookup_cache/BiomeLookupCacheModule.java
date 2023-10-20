package io.github.sakurawald.module.biome_lookup_cache;

import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;

import java.util.function.Supplier;

public class BiomeLookupCacheModule extends AbstractModule {
    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.optimizationWrapper.instance().optimization.spawn.fastBiomeLookup;
    }
}
