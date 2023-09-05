package fun.sakurawald.resource_world;

import net.minecraft.world.dimension.DimensionOptions;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;

@ApiStatus.Internal
public interface FantasyDimensionOptions {
    Predicate<DimensionOptions> SAVE_PROPERTIES_PREDICATE = (e) -> ((FantasyDimensionOptions) (Object) e).sakurawald$getSaveProperties();

    void sakurawald$setSaveProperties(boolean value);

    boolean sakurawald$getSaveProperties();
}
