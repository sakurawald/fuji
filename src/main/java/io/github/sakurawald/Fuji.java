package io.github.sakurawald;

import io.github.sakurawald.core.command.processor.CommandAnnotationProcessor;
import io.github.sakurawald.core.manager.Managers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

// TODO: rank module (track, requirement)
// TODO: hologram module (facility)
// TODO: spawn/respawn control

// TODO: command attachment for entity, and block
// TODO: argument resolver for command alias module

// TODO: closure factory method for AbstractJob
// TODO: curse forge plugin

// TODO: a lisp-like DSL (parser, code-walker, transformer, analyzer, nbt selector, predicate, equal)
// TODO: command combination

// TODO: a `common` package in mixin package
// TODO: manifold getInstance()

public class Fuji implements ModInitializer {

    public static final String MOD_ID = "fuji";
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toAbsolutePath();

    @Override
    public void onInitialize() {
        Managers.getStandardBackupManager().onInitialize();
        Managers.getBossBarManager().onInitialize();
        Managers.getModuleManager().onInitialize();
        CommandAnnotationProcessor.process();
        Managers.getScheduleManager().onInitialize();
    }
}
