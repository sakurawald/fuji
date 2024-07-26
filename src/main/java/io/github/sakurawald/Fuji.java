package io.github.sakurawald;

import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.common.manager.BackupManager;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.util.ScheduleUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

import static io.github.sakurawald.module.ModuleManager.initializeModules;

// TODO: rank module (track, requirement)
// TODO: spawn module (facility)
// TODO: hologram module (facility)
// TODO: invsee module (slot, inv-redirect)
// TODO: powertool module (composed, meta)

// TODO: a lisp-like DSL (parser, code-walker, transformer, nbt selector)
// TODO: refactor command facility (tui, selector, aop, options, parser, redirect/option, modifier, operation-argument-type, suggestion)
// TODO: tppos module

public class Fuji implements ModInitializer {

    public static final String MOD_ID = "fuji";
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toAbsolutePath();

    @Override
    public void onInitialize() {
        /* managers */
        BackupManager.backup();
        Managers.getBossBarManager().initialize();

        /* modules */
        initializeModules();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ModuleManager.reportModules());

        /* scheduler */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ScheduleUtil.startScheduler());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ScheduleUtil.triggerJobs(ConfigHandler.ConfigHandlerAutoSaveJob.class.getName());
            ScheduleUtil.shutdownScheduler();
        });
    }
}
