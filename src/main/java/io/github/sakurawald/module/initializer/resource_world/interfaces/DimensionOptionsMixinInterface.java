package io.github.sakurawald.module.initializer.resource_world.interfaces;

import net.minecraft.world.dimension.DimensionOptions;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;

@ApiStatus.Internal
public interface DimensionOptionsMixinInterface {
    Predicate<DimensionOptions> SAVE_PROPERTIES_PREDICATE = (e) -> ((DimensionOptionsMixinInterface) (Object) e).fuji$getSaveProperties();

    void fuji$setSaveProperties(boolean value);

    boolean fuji$getSaveProperties();
}
