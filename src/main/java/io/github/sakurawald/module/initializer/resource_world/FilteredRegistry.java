package io.github.sakurawald.module.initializer.resource_world;

import com.google.common.collect.Iterators;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "InfiniteRecursion", "LombokGetterMayBeUsed"})
public class FilteredRegistry<T> extends MappedRegistry<T> {
    private final Registry<T> source;
    private final Predicate<T> check;

    public FilteredRegistry(Registry<T> source, Predicate<T> check) {
        super(source.key(), source.registryLifecycle());
        this.source = source;
        this.check = check;
    }

    public Registry<T> getSource() {
        return this.source;
    }

    @Nullable
    @Override
    public ResourceLocation getKey(T value) {
        return check.test(value) ? this.source.getKey(value) : null;
    }

    @Override
    public Optional<ResourceKey<T>> getResourceKey(T entry) {
        return check.test(entry) ? this.source.getResourceKey(entry) : Optional.empty();
    }

    @Override
    public int getId(@Nullable T value) {
        return check.test(value) ? this.source.getId(value) : -1;
    }

    @Nullable
    @Override
    public T byId(int index) {
        return this.source.byId(index);
    }

    @Override
    public int size() {
        return this.source.size();
    }

    @Nullable
    @Override
    public T get(@Nullable ResourceKey<T> key) {
        return this.source.get(key);
    }

    @Nullable
    @Override
    public T get(@Nullable ResourceLocation id) {
        return this.get(id);
    }

    @Override
    public Lifecycle lifecycle(T entry) {
        return this.source.lifecycle(entry);
    }

    @Override
    public Lifecycle registryLifecycle() {
        return this.source.registryLifecycle();
    }

    @Override
    public Set<ResourceLocation> keySet() {
        return this.keySet();
    }

    @Override
    public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
        Set<Map.Entry<ResourceKey<T>, T>> set = new HashSet<>();
        for (Map.Entry<ResourceKey<T>, T> e : this.source.entrySet()) {
            if (this.check.test(e.getValue())) {
                set.add(e);
            }
        }
        return set;
    }

    @Override
    public Set<ResourceKey<T>> registryKeySet() {
        return null;
    }

    @Override
    public Optional<Holder.Reference<T>> getRandom(net.minecraft.util.RandomSource random) {
        return Optional.empty();
    }

    @Override
    public boolean containsKey(ResourceLocation id) {
        return this.source.containsKey(id);
    }

    @Override
    public boolean containsKey(ResourceKey<T> key) {
        return this.source.containsKey(key);
    }

    @Override
    public Registry<T> freeze() {
        return this;
    }

    @Override
    public Holder.Reference<T> createIntrusiveHolder(T value) {
        return null;
    }

    @Override
    public Optional<Holder.Reference<T>> getHolder(int rawId) {
        return this.source.getHolder(rawId);
    }

    @Override
    public Optional<Holder.Reference<T>> getHolder(ResourceKey<T> key) {
        return this.source.getHolder(key);
    }

    @Override
    public Stream<Holder.Reference<T>> holders() {
        return this.source.holders().filter((e) -> this.check.test(e.value()));
    }

    @Override
    public Optional<HolderSet.Named<T>> getTag(TagKey<T> tag) {
        return Optional.empty();
    }

    @Override
    public HolderSet.Named<T> getOrCreateTag(TagKey<T> tag) {
        return null;
    }

    @Override
    public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
        return null;
    }

    @Override
    public Stream<TagKey<T>> getTagNames() {
        return null;
    }

    @Override
    public void resetTags() {

    }

    @Override
    public void bindTags(Map<TagKey<T>, List<Holder<T>>> tagEntries) {

    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return Iterators.filter(this.source.iterator(), this.check::test);
    }
}
