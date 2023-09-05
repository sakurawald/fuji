package fun.sakurawald.mixin.registry;

import fun.sakurawald.resource_world.FantasyDimensionOptions;
import net.minecraft.world.dimension.DimensionOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DimensionOptions.class)
public class DimensionOptionsMixin implements FantasyDimensionOptions {

    @Unique
    private boolean sakurawald$saveProperties = true;

    @Unique
    public void sakurawald$setSaveProperties(boolean value) {
        this.sakurawald$saveProperties = value;
    }

    @Unique
    public boolean sakurawald$getSaveProperties() {
        return this.sakurawald$saveProperties;
    }
}
