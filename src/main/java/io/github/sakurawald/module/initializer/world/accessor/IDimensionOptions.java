package io.github.sakurawald.module.initializer.world.accessor;

import net.minecraft.world.dimension.DimensionOptions;

import java.util.function.Predicate;

public interface IDimensionOptions {
    Predicate<DimensionOptions> SAVE_PROPERTIES_PREDICATE = (e) -> ((IDimensionOptions) (Object) e).fuji$getSaveProperties();

    void fuji$setSaveProperties(boolean value);

    boolean fuji$getSaveProperties();
}
