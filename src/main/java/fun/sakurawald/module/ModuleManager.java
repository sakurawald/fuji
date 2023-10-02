package fun.sakurawald.module;

import fun.sakurawald.module.back.BackModule;
import fun.sakurawald.module.better_fake_player.BetterFakePlayerModule;
import fun.sakurawald.module.chat_style.ChatStyleModule;
import fun.sakurawald.module.command_cooldown.CommandCooldownModule;
import fun.sakurawald.module.config.ConfigModule;
import fun.sakurawald.module.deathlog.DeathLogModule;
import fun.sakurawald.module.display.DisplayModule;
import fun.sakurawald.module.head.HeadModule;
import fun.sakurawald.module.main_stats.MainStatsModule;
import fun.sakurawald.module.motd.MotdModule;
import fun.sakurawald.module.newbie_welcome.NewbieWelcomeModule;
import fun.sakurawald.module.pvp_toggle.PvpModule;
import fun.sakurawald.module.resource_world.ResourceWorldModule;
import fun.sakurawald.module.skin.command.SkinModule;
import fun.sakurawald.module.teleport_warmup.TeleportWarmupModule;
import fun.sakurawald.module.top_chunks.TopChunksModule;
import fun.sakurawald.module.tpa.TpaModule;
import fun.sakurawald.module.works.WorksModule;
import fun.sakurawald.module.world_downloader.WorldDownloaderModule;
import fun.sakurawald.module.zero_command_permission.ZeroCommandPermissionModule;
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
        getOrNewInstance(DisplayModule.class);
        getOrNewInstance(NewbieWelcomeModule.class);
        getOrNewInstance(CommandCooldownModule.class);
        getOrNewInstance(MotdModule.class);
        getOrNewInstance(HeadModule.class);
        getOrNewInstance(ZeroCommandPermissionModule.class);
    }

    public static void reloadModules() {
        instances.values().forEach(AbstractModule::onReload);
    }

    public static <T extends AbstractModule> T getOrNewInstance(Class<T> clazz) {
        if (!instances.containsKey(clazz)) {
            log.info("Load module -> {}", clazz.getSimpleName());
            try {
                AbstractModule abstractModule = clazz.getDeclaredConstructor().newInstance();
                // initialize module here.
                abstractModule.onInitialize();
                instances.put(clazz, abstractModule);
            } catch (Exception e) {
                log.warn(e.toString());
            }
        }
        return clazz.cast(instances.get(clazz));
    }
}
