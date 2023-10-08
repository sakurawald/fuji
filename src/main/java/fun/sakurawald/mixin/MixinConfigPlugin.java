package fun.sakurawald.mixin;

import fun.sakurawald.config.ConfigWrapper;
import fun.sakurawald.config.OptimizationGSON;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

@Slf4j
public class MixinConfigPlugin implements IMixinConfigPlugin {

    public static final ConfigWrapper<OptimizationGSON> optimizationWrapper = new ConfigWrapper<>("optimization.json", OptimizationGSON.class);

    @Override
    public void onLoad(String mixinPackage) {
        // note: this method is called before the mixin config is read
        optimizationWrapper.loadFromDisk();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String PACKAGE_PREFIX = "fun.sakurawald.mixin.";

        if (mixinClassName.equals(PACKAGE_PREFIX + "biome_lookup_cache.NaturalSpawnerMixin")) {
            return optimizationWrapper.instance().optimization.spawn.fastBiomeLookup;
        }

        if (mixinClassName.equals(PACKAGE_PREFIX + "tick_chunk_cache.ServerChunkCacheMixin")
                || mixinClassName.equals(PACKAGE_PREFIX + "tick_chunk_cache.ChunkMapMixin")) {
            return optimizationWrapper.instance().optimization.chunk.fastTickChunk;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
