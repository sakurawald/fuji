package io.github.sakurawald.mixin;

import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.config.ConfigWrapper;
import io.github.sakurawald.config.OptimizationGSON;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Slf4j
public class MixinConfigPlugin implements IMixinConfigPlugin {
    public static final ConfigWrapper<OptimizationGSON> optimizationWrapper = new ConfigWrapper<>("optimization.json", OptimizationGSON.class);

    static {
        ConfigManager.configWrapper.loadFromDisk();
    }

    private final int MIXIN_PACKAGE_NAME_LEFT = this.getClass().getPackageName().length() + 1;
    private final HashMap<String, Supplier<Boolean>> mixinConfig = new HashMap<>() {
        {
            this.put("back", () -> ConfigManager.configWrapper.instance().modules.back.enable);
            this.put("better_fake_player", () -> ConfigManager.configWrapper.instance().modules.better_fake_player.enable);
            this.put("better_info", () -> ConfigManager.configWrapper.instance().modules.better_info.enable);
            this.put("biome_lookup_cache", () -> optimizationWrapper.instance().optimization.spawn.fastBiomeLookup);
            this.put("bypass_things.chat_speed", () -> ConfigManager.configWrapper.instance().modules.bypass_things.bypass_chat_speed.enable);
            this.put("bypass_things.move_speed", () -> ConfigManager.configWrapper.instance().modules.bypass_things.bypass_move_speed.enable);
            this.put("bypass_things.player_limit", () -> ConfigManager.configWrapper.instance().modules.bypass_things.bypass_player_limit.enable);
            this.put("chat_style", () -> ConfigManager.configWrapper.instance().modules.chat_style.enable);
            this.put("command_cooldown", () -> ConfigManager.configWrapper.instance().modules.command_cooldown.enable);
            this.put("deathlog", () -> ConfigManager.configWrapper.instance().modules.death_log.enable);
            this.put("dynamic_motd", () -> ConfigManager.configWrapper.instance().modules.dynamic_motd.enable);
            this.put("main_stats", () -> ConfigManager.configWrapper.instance().modules.main_stats.enable);
            this.put("multi_obsidian_platform", () -> ConfigManager.configWrapper.instance().modules.multi_obsidian_platform.enable);
            this.put("newbie_welcome", () -> ConfigManager.configWrapper.instance().modules.newbie_welcome.enable);
            this.put("op_protect", () -> ConfigManager.configWrapper.instance().modules.op_protect.enable);
            this.put("pvp_toggle", () -> ConfigManager.configWrapper.instance().modules.pvp_toggle.enable);
            this.put("resource_world", () -> ConfigManager.configWrapper.instance().modules.resource_world.enable);
            this.put("skin", () -> ConfigManager.configWrapper.instance().modules.skin.enable);
            this.put("stronger_player_list", () -> ConfigManager.configWrapper.instance().modules.stronger_player_list.enable);
            this.put("teleport_warmup", () -> ConfigManager.configWrapper.instance().modules.teleport_warmup.enable);
            this.put("tick_chunk_cache", () -> optimizationWrapper.instance().optimization.chunk.fastTickChunk);
            this.put("top_chunks", () -> ConfigManager.configWrapper.instance().modules.top_chunks.enable);
            this.put("whitelist_fix", () -> ConfigManager.configWrapper.instance().modules.whitelist_fix.enable);
            this.put("works", () -> ConfigManager.configWrapper.instance().modules.works.enable);
            this.put("zero_command_permission", () -> ConfigManager.configWrapper.instance().modules.zero_command_permission.enable);
            this.put("command_spy", () -> ConfigManager.configWrapper.instance().modules.command_spy.enable);
        }
    };

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
        int right = mixinClassName.lastIndexOf(".");
        String[] mixinPackageNames = mixinClassName.substring(MIXIN_PACKAGE_NAME_LEFT, right).split("\\.");
        String mixinPackageName = null;

        for (String key : mixinPackageNames) {
            if (mixinPackageName == null) {
                mixinPackageName = key;
            } else mixinPackageName += "." + key;

            // note: if there is no config for this mixin, then we enable this mixin by default
            if (this.mixinConfig.getOrDefault(mixinPackageName, () -> true).get()) return true;
        }

        log.warn("Disable mixin {}", mixinClassName);
        return false;
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
