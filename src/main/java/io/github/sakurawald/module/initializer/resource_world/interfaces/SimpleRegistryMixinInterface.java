package io.github.sakurawald.module.initializer.resource_world.interfaces;

import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SimpleRegistryMixinInterface<T> {
    @SuppressWarnings("unchecked")
    static <T> boolean remove(SimpleRegistry<T> registry, Identifier key) {
        return ((SimpleRegistryMixinInterface<T>) registry).fuji$remove(key);
    }

    @SuppressWarnings("unchecked")
    static <T> boolean remove(SimpleRegistry<T> registry, T value) {
        return ((SimpleRegistryMixinInterface<T>) registry).fuji$remove(value);
    }

    boolean fuji$remove(T value);

    boolean fuji$remove(Identifier key);

    void fuji$setFrozen(boolean value);

    boolean fuji$isFrozen();
}
