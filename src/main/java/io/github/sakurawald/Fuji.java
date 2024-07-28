package io.github.sakurawald;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.common.manager.Managers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

// TODO: rank module (track, requirement)
// TODO: spawn module (facility)
// TODO: hologram module (facility)
// TODO: invsee module (slot, inv-redirect)
// TODO: powertool module (composed, meta)

// TODO: a lisp-like DSL (parser, code-walker, transformer, analyzer, nbt selector)
// TODO: refactor command facility (tui, selector, aop, options, parser, redirect/option, modifier, operation-argument-type, suggestion)
// TODO: tppos module

public class Fuji implements ModInitializer {

    public static final String MOD_ID = "fuji";
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toAbsolutePath();

    @Override
    public void onInitialize() {
        /* managers */
        Managers.getStandardBackupManager().backup();
        Managers.getBossBarManager().initialize();
        Managers.getScheduleManager().initialize();

        /* modules */
        ModuleManager.initialize();
    }
}
