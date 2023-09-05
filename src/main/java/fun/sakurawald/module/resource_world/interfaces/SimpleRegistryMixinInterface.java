package fun.sakurawald.module.resource_world.interfaces;

import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SimpleRegistryMixinInterface<T> {
    @SuppressWarnings("unchecked")
    static <T> boolean remove(SimpleRegistry<T> registry, Identifier key) {
        return ((SimpleRegistryMixinInterface<T>) registry).sakurawald$remove(key);
    }

    @SuppressWarnings("unchecked")
    static <T> boolean remove(SimpleRegistry<T> registry, T value) {
        return ((SimpleRegistryMixinInterface<T>) registry).sakurawald$remove(value);
    }

    boolean sakurawald$remove(T value);

    boolean sakurawald$remove(Identifier key);

    void sakurawald$setFrozen(boolean value);

    boolean sakurawald$isFrozen();
}
