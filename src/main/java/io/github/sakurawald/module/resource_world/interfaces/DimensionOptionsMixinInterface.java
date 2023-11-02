package io.github.sakurawald.module.resource_world.interfaces;

import net.minecraft.world.level.dimension.LevelStem;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;

@ApiStatus.Internal
public interface DimensionOptionsMixinInterface {
    Predicate<LevelStem> SAVE_PROPERTIES_PREDICATE = (e) -> ((DimensionOptionsMixinInterface) (Object) e).fuji$getSaveProperties();

    void fuji$setSaveProperties(boolean value);

    boolean fuji$getSaveProperties();
}
