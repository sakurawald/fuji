package io.github.sakurawald.module.initializer.resource_world;

import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.WorldData;

/**
 * The only purpose of this class is to warp the seed.
 **/
@SuppressWarnings("LombokGetterMayBeUsed")
public final class ResourceWorldProperties extends DerivedLevelData {

    private final long seed;

    public ResourceWorldProperties(WorldData saveProperties, long seed) {
        super(saveProperties, saveProperties.overworldData());
        this.seed = seed;
    }

    public long getSeed() {
        return seed;
    }
}
