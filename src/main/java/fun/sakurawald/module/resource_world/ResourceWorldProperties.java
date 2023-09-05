package fun.sakurawald.module.resource_world;

import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;

/**
 * The only purpose of this class is to warp the seed.
 **/
public final class ResourceWorldProperties extends UnmodifiableLevelProperties {

    private final long seed;

    public ResourceWorldProperties(SaveProperties saveProperties, long seed) {
        super(saveProperties, saveProperties.getMainWorldProperties());
        this.seed = seed;
    }

    public long getSeed() {
        return seed;
    }
}
