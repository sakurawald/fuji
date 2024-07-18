package io.github.sakurawald.module.mixin.resource_world;

import io.github.sakurawald.module.initializer.resource_world.interfaces.DimensionOptionsMixinInterface;
import net.minecraft.world.dimension.DimensionOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DimensionOptions.class)
public class DimensionOptionsMixin implements DimensionOptionsMixinInterface {

    @Unique
    private boolean fuji$saveProperties = true;

    @Unique
    public void fuji$setSaveProperties(boolean value) {
        this.fuji$saveProperties = value;
    }

    @Unique
    public boolean fuji$getSaveProperties() {
        return this.fuji$saveProperties;
    }
}
