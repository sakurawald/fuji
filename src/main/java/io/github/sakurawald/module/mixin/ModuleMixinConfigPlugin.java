package io.github.sakurawald.module.mixin;

import com.google.gson.JsonElement;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.ModuleManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;


public class ModuleMixinConfigPlugin implements IMixinConfigPlugin {

    private static final JsonElement mixinConfigs;

    static {
        ConfigManager.configWrapper.loadFromDisk();
        mixinConfigs = ConfigManager.configWrapper.toJsonElement();
    }

    @Override
    public void onLoad(String mixinPackage) {
        // no-op
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String basePackageName = ModuleManager.calculateBasePackageName(this.getClass(), mixinClassName);
        if (basePackageName.startsWith("_")) return true;
        return ModuleManager.enableModule(mixinConfigs, basePackageName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // no-op
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // no-op
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // no-op
    }
}
