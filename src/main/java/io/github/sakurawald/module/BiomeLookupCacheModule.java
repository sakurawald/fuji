package io.github.sakurawald.module;

import io.github.sakurawald.config.ConfigManager;

import java.util.function.Supplier;

public class BiomeLookupCacheModule extends AbstractModule {
    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.optimizationWrapper.instance().optimization.spawn.fastBiomeLookup;
    }
}
