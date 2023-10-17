package io.github.sakurawald.mixin.resource_world.registry;

import io.github.sakurawald.module.resource_world.interfaces.DimensionOptionsMixinInterface;
import net.minecraft.world.level.dimension.LevelStem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelStem.class)
public class DimensionOptionsMixin implements DimensionOptionsMixinInterface {

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
