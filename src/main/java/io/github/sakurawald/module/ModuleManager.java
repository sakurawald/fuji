package io.github.sakurawald.module;

import io.github.sakurawald.module.biome_lookup_cache.BiomeLookupCacheModule;
import io.github.sakurawald.module.back.BackModule;
import io.github.sakurawald.module.better_fake_player.BetterFakePlayerModule;
import io.github.sakurawald.module.better_info.BetterInfoModule;
import io.github.sakurawald.module.bypass_things.chat_speed.BypassThingsChatSpeedModule;
import io.github.sakurawald.module.bypass_things.move_speed.BypassThingsMoveSpeedModule;
import io.github.sakurawald.module.bypass_things.player_limit.BypassThingsPlayerLimitModule;
import io.github.sakurawald.module.chat_style.ChatStyleModule;
import io.github.sakurawald.module.command_cooldown.CommandCooldownModule;
import io.github.sakurawald.module.command_spy.CommandSpyModule;
import io.github.sakurawald.module.config.ConfigModule;
import io.github.sakurawald.module.deathlog.DeathLogModule;
import io.github.sakurawald.module.head.HeadModule;
import io.github.sakurawald.module.main_stats.MainStatsModule;
import io.github.sakurawald.module.motd.DynamicMotdModule;
import io.github.sakurawald.module.multi_obsidian_platform.MultiObsidianPlatformModule;
import io.github.sakurawald.module.newbie_welcome.NewbieWelcomeModule;
import io.github.sakurawald.module.op_protect.OpProtectModule;
import io.github.sakurawald.module.profiler.ProfilerModule;
import io.github.sakurawald.module.pvp_toggle.PvpModule;
import io.github.sakurawald.module.resource_world.ResourceWorldModule;
import io.github.sakurawald.module.skin.command.SkinModule;
import io.github.sakurawald.module.stronger_player_list.StrongerPlayerListModule;
import io.github.sakurawald.module.teleport_warmup.TeleportWarmupModule;
import io.github.sakurawald.module.test.TestModule;
import io.github.sakurawald.module.tick_chunk_cache.TickChunkCacheModule;
import io.github.sakurawald.module.top_chunks.TopChunksModule;
import io.github.sakurawald.module.tpa.TpaModule;
import io.github.sakurawald.module.whitelist_fix.WhitelistFixModule;
import io.github.sakurawald.module.works.WorksModule;
import io.github.sakurawald.module.world_downloader.WorldDownloaderModule;
import io.github.sakurawald.module.zero_command_permission.ZeroCommandPermissionModule;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ModuleManager {
    private static final Map<Class<? extends AbstractModule>, AbstractModule> instances = new HashMap<>();

    public static void initializeModules() {
        getOrNewInstance(ConfigModule.class);
        getOrNewInstance(PvpModule.class);
        getOrNewInstance(ResourceWorldModule.class);
        getOrNewInstance(TopChunksModule.class);
        getOrNewInstance(BetterFakePlayerModule.class);
        getOrNewInstance(TeleportWarmupModule.class);
        getOrNewInstance(ChatStyleModule.class);
        getOrNewInstance(SkinModule.class);
        getOrNewInstance(DeathLogModule.class);
        getOrNewInstance(BackModule.class);
        getOrNewInstance(TpaModule.class);
        getOrNewInstance(WorksModule.class);
        getOrNewInstance(WorldDownloaderModule.class);
        getOrNewInstance(MainStatsModule.class);
        getOrNewInstance(NewbieWelcomeModule.class);
        getOrNewInstance(CommandCooldownModule.class);
        getOrNewInstance(CommandSpyModule.class);
        getOrNewInstance(DynamicMotdModule.class);
        getOrNewInstance(HeadModule.class);
        getOrNewInstance(TestModule.class);
        getOrNewInstance(ProfilerModule.class);
        getOrNewInstance(BetterInfoModule.class);
        getOrNewInstance(BiomeLookupCacheModule.class);
        getOrNewInstance(TickChunkCacheModule.class);
        getOrNewInstance(MultiObsidianPlatformModule.class);
        getOrNewInstance(BypassThingsChatSpeedModule.class);
        getOrNewInstance(BypassThingsMoveSpeedModule.class);
        getOrNewInstance(BypassThingsPlayerLimitModule.class);
        getOrNewInstance(OpProtectModule.class);
        getOrNewInstance(StrongerPlayerListModule.class);
        getOrNewInstance(WhitelistFixModule.class);
        getOrNewInstance(ZeroCommandPermissionModule.class);
    }

    public static void reloadModules() {
        instances.values().forEach(AbstractModule::onReload);
    }

    /**
     * if a module is disabled, then this method will return null
     */
    public static <T extends AbstractModule> T getOrNewInstance(Class<T> clazz) {
        if (!instances.containsKey(clazz)) {
            String moduleName = clazz.getSimpleName();
            try {
                AbstractModule abstractModule = clazz.getDeclaredConstructor().newInstance();
                if (abstractModule.enableModule().get()) {
                    log.info("Initialize module -> {}", moduleName);
                    // initialize module here.
                    abstractModule.onInitialize();
                    instances.put(clazz, abstractModule);
                } else {
                    log.warn("Skip-Initialize module -> {}", moduleName);
                }

            } catch (Exception e) {
                log.warn(e.toString());
            }
        }
        return clazz.cast(instances.get(clazz));
    }
}
