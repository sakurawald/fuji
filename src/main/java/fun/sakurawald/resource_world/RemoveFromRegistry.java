package fun.sakurawald.resource_world;

import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface RemoveFromRegistry<T> {
    @SuppressWarnings({"UnusedReturnValue", "unchecked"})
    static <T> boolean sakurawald$remove(SimpleRegistry<T> registry, Identifier key) {
        return ((RemoveFromRegistry<T>) registry).sakurawald$remove(key);
    }

    boolean sakurawald$remove(T value);

    boolean sakurawald$remove(Identifier key);

}
