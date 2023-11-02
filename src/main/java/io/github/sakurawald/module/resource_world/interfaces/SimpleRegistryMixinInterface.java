package io.github.sakurawald.module.resource_world.interfaces;

import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SimpleRegistryMixinInterface<T> {
    @SuppressWarnings("unchecked")
    static <T> boolean remove(MappedRegistry<T> registry, ResourceLocation key) {
        return ((SimpleRegistryMixinInterface<T>) registry).fuji$remove(key);
    }

    @SuppressWarnings("unchecked")
    static <T> boolean remove(MappedRegistry<T> registry, T value) {
        return ((SimpleRegistryMixinInterface<T>) registry).fuji$remove(value);
    }

    boolean fuji$remove(T value);

    boolean fuji$remove(ResourceLocation key);

    void fuji$setFrozen(boolean value);

    boolean fuji$isFrozen();
}
