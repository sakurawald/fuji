package fun.sakurawald.module.resource_world.interfaces;

import net.minecraft.world.dimension.DimensionOptions;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;

@ApiStatus.Internal
public interface DimensionOptionsMixinInterface {
    Predicate<DimensionOptions> SAVE_PROPERTIES_PREDICATE = (e) -> ((DimensionOptionsMixinInterface) (Object) e).sakurawald$getSaveProperties();

    void sakurawald$setSaveProperties(boolean value);

    boolean sakurawald$getSaveProperties();
}
