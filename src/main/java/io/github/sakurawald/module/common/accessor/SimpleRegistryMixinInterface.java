package io.github.sakurawald.module.common.accessor;

import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface SimpleRegistryMixinInterface<T> {
    @SuppressWarnings("unchecked")
    static <T> boolean remove(@NotNull SimpleRegistry<T> registry, Identifier key) {
        return ((SimpleRegistryMixinInterface<T>) registry).fuji$remove(key);
    }

    @SuppressWarnings("unchecked")
    static <T> boolean remove(@NotNull SimpleRegistry<T> registry, T value) {
        return ((SimpleRegistryMixinInterface<T>) registry).fuji$remove(value);
    }

    boolean fuji$remove(T value);

    boolean fuji$remove(Identifier key);

    void fuji$setFrozen(boolean value);

    boolean fuji$isFrozen();
}
