package fun.sakurawald.mixin.resource_world.registry;

import com.mojang.serialization.Lifecycle;
import fun.sakurawald.module.resource_world.interfaces.SimpleRegistryMixinInterface;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
@Mixin(MappedRegistry.class)
public abstract class SimpleRegistryMixin<T> implements SimpleRegistryMixinInterface<T> {

    @Shadow
    @Final
    private Map<T, Holder.Reference<T>> byValue;

    @Shadow
    @Final
    private Map<ResourceLocation, Holder.Reference<T>> byLocation;

    @Shadow
    @Final
    private Map<ResourceKey<T>, Holder.Reference<T>> byKey;

    @Shadow
    @Final
    private Map<T, Lifecycle> lifecycles;

    @Shadow
    @Final
    private ObjectList<Holder.Reference<T>> byId;

    @Shadow
    @Final
    private Object2IntMap<T> toId;
    @Shadow
    private boolean frozen;
    @Shadow
    @Nullable
    private List<Holder.Reference<T>> holdersInOrder;

    @Shadow
    public abstract Optional<Holder<T>> getHolder(int rawId);

    @Override
    public boolean sakurawald$remove(T entry) {
        var registryEntry = this.byValue.get(entry);
        int rawId = this.toId.removeInt(entry);
        if (rawId == -1) {
            return false;
        }

        try {
            this.byId.set(rawId, null);
            this.byLocation.remove(registryEntry.key().location());
            this.byKey.remove(registryEntry.key());
            this.lifecycles.remove(entry);
            this.byValue.remove(entry);
            if (this.holdersInOrder != null) {
                this.holdersInOrder.remove(registryEntry);
            }

            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean sakurawald$remove(ResourceLocation key) {
        var entry = this.byLocation.get(key);
        return entry != null && entry.isBound() && this.sakurawald$remove(entry.value());
    }

    @Override
    public void sakurawald$setFrozen(boolean value) {
        this.frozen = value;
    }

    @Override
    public boolean sakurawald$isFrozen() {
        return this.frozen;
    }
}
