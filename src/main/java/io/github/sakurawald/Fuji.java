package io.github.sakurawald;

import io.github.sakurawald.command.processor.CommandAnnotationProcessor;
import io.github.sakurawald.module.common.manager.Managers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

// TODO: rank module (track, requirement)
// TODO: hologram module (facility)
// TODO: invsee module (slot, inv-redirect)
// TODO: move docs gen into another project

// TODO: powertool module (composed, meta)
// TODO: unified player data api (persistent data container -> attachment: subject)

// TODO: a lisp-like DSL (parser, code-walker, transformer, analyzer, nbt selector)

public class Fuji implements ModInitializer {

    public static final String MOD_ID = "fuji";
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toAbsolutePath();

    @Override
    public void onInitialize() {
        Managers.getStandardBackupManager().backup();
        Managers.getBossBarManager().onInitialize();
        Managers.getModuleManager().onInitialize();
        CommandAnnotationProcessor.process();
        Managers.getScheduleManager().onInitialize();
    }
}
