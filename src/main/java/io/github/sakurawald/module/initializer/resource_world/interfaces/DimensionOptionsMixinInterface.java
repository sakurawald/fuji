package io.github.sakurawald.module.initializer.resource_world.interfaces;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;
import net.minecraft.world.dimension.DimensionOptions;

@ApiStatus.Internal
public interface DimensionOptionsMixinInterface {
    Predicate<DimensionOptions> SAVE_PROPERTIES_PREDICATE = (e) -> ((DimensionOptionsMixinInterface) (Object) e).fuji$getSaveProperties();

    void fuji$setSaveProperties(boolean value);

    boolean fuji$getSaveProperties();
}
