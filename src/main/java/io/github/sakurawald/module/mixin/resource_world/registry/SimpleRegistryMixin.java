package io.github.sakurawald.module.mixin.resource_world.registry;

import com.mojang.serialization.Lifecycle;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.resource_world.interfaces.SimpleRegistryMixinInterface;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
@Mixin(SimpleRegistry.class)

public abstract class SimpleRegistryMixin<T> implements SimpleRegistryMixinInterface<T> {

    @Shadow
    @Final
    private Map<T, RegistryEntry.Reference<T>> valueToEntry;

    @Shadow
    @Final
    private Map<Identifier, RegistryEntry.Reference<T>> idToEntry;

    @Shadow
    @Final
    private Map<RegistryKey<T>, RegistryEntry.Reference<T>> keyToEntry;

    @Shadow
    @Final
    private Map<T, Lifecycle> entryToLifecycle;

    @Shadow
    @Final
    private ObjectList<RegistryEntry.Reference<T>> rawIdToEntry;

    @Shadow
    @Final
    private Reference2IntMap<T> entryToRawId;

    @Shadow
    private boolean frozen;
    @Shadow
    @Nullable
    private List<RegistryEntry.Reference<T>> cachedEntries;

    @Shadow
    public abstract Optional<RegistryEntry<T>> getEntry(int rawId);

    @Override
    public boolean fuji$remove(T entry) {
        var registryEntry = this.valueToEntry.get(entry);
        int rawId = this.entryToRawId.removeInt(entry);
        if (rawId == -1) {
            return false;
        }

        try {
            this.rawIdToEntry.set(rawId, null);
            this.idToEntry.remove(registryEntry.registryKey().getValue());
            this.keyToEntry.remove(registryEntry.registryKey());
            this.entryToLifecycle.remove(entry);
            this.valueToEntry.remove(entry);
            if (this.cachedEntries != null) {
                this.cachedEntries.remove(registryEntry);
            }

            return true;
        } catch (Throwable e) {
            Fuji.LOGGER.error("Failed to remove entry: " + entry.toString());
            return false;
        }
    }

    @Override
    public boolean fuji$remove(Identifier key) {
        var entry = this.idToEntry.get(key);
        return entry != null && entry.hasKeyAndValue() && this.fuji$remove(entry.comp_349());
    }

    @Override
    public void fuji$setFrozen(boolean value) {
        this.frozen = value;
    }

    @Override
    public boolean fuji$isFrozen() {
        return this.frozen;
    }
}
