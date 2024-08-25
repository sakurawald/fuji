package io.github.sakurawald;

import io.github.sakurawald.command.processor.CommandAnnotationProcessor;
import io.github.sakurawald.module.common.manager.Managers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

// TODO: rank module (track, requirement)
// TODO: hologram module (facility)
// TODO: invsee module (slot, inv-redirect, inv, ender)
// TODO: move docs gen into another project

// TODO: a lisp-like DSL (parser, code-walker, transformer, analyzer, nbt selector, predicate, equal)

// TODO: a `common` package in mixin package

// TODO: /mail
// TODO: command attachment for entity, and block
// TODO: closure factory method for AbstractJob
// TODO: argument resolver for command alias module
// TODO: rename to specialized command
// TODO: /delay --name, /delay list, /delay cancel
// TODO: /for do
// TODO: partial async for rtp

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
