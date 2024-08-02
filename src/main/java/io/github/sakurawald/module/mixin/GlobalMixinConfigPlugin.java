package io.github.sakurawald.module.mixin;

import com.google.gson.JsonElement;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.manager.Managers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;


public class GlobalMixinConfigPlugin implements IMixinConfigPlugin {

    private static final JsonElement rcConfig;

    static {
        Configs.configHandler.loadFromDisk();
        rcConfig = Configs.configHandler.toJsonElement();
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, @NotNull String mixinClassName) {
        List<String> dirNameList = Managers.getModuleManager().getDirNameList(this.getClass(), mixinClassName);

        // bypass
        if (dirNameList.getFirst().startsWith("_")) return true;

        return Managers.getModuleManager().shouldWeEnableThisModule(rcConfig, dirNameList);
    }

    @Override
    public void onLoad(String mixinPackage) {
        // no-op
    }

    @Override
    public @Nullable String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // no-op
    }

    @Override
    public @Nullable List<String> getMixins() {
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
