package io.github.sakurawald;

import io.github.sakurawald.command.processor.BrigadierAnnotationProcessor;
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
// TODO: tppos module
// TODO: unified player data api (persistent data container)
// TODO: move docs gen into another project
// TODO: supplier for scheduler
// TODO: refactor command facility (tui, selector, aop, options, parser, redirect/option, modifier, operation-argument-type, suggestion)
// TODO: cfg gradle shadow plugin

public class Fuji implements ModInitializer {

    public static final String MOD_ID = "fuji";
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toAbsolutePath();

    @Override
    public void onInitialize() {
        Managers.getStandardBackupManager().backup();
        Managers.getBossBarManager().onInitialize();
        Managers.getModuleManager().onInitialize();
        BrigadierAnnotationProcessor.register();
        Managers.getScheduleManager().onInitialize();
    }
}
