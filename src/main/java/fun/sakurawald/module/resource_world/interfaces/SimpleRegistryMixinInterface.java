package fun.sakurawald.module.resource_world.interfaces;

import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SimpleRegistryMixinInterface<T> {
    @SuppressWarnings("unchecked")
    static <T> boolean remove(MappedRegistry<T> registry, ResourceLocation key) {
        return ((SimpleRegistryMixinInterface<T>) registry).sakurawald$remove(key);
    }

    @SuppressWarnings("unchecked")
    static <T> boolean remove(MappedRegistry<T> registry, T value) {
        return ((SimpleRegistryMixinInterface<T>) registry).sakurawald$remove(value);
    }

    boolean sakurawald$remove(T value);

    boolean sakurawald$remove(ResourceLocation key);

    void sakurawald$setFrozen(boolean value);

    boolean sakurawald$isFrozen();
}
