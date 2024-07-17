package io.github.sakurawald;

import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.common.service.BackupService;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.ScheduleUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

// TODO: rank module (track)
// TODO: tab list module
// TODO: spawn module
// TODO: command warmup module
// TODO: tppos module
// TODO: invsee module
// TODO: condense module
// TODO: powertool modul
// TODO: code review for skin module
// TODO: hologram module

// TODO: a lisp-like DSL (parser and code-walker) for specific command with context and placeholders (%fuji:play_time_total%).
// TODO: refactor command facility (selector, aop, options, parser)
// TODO: specific command module
// TODO: add native shell support specific command
// TODO: kit module -> spec-command

// TODO: a generalized mixin plugin to dispatch text.
// TODO: a program to generate module reference DAG
// TODO: a program to generate config json with documentation

public class Fuji implements ModInitializer {
    public static final String MOD_ID = "fuji";
    public static final Logger LOGGER = LogUtil.createLogger(StringUtils.capitalize(MOD_ID));
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toAbsolutePath();
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        /* backup */
        BackupService.backup();

        /* modules */
        ModuleManager.initializeModules();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ModuleManager.reportModules());

        /* scheduler */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ScheduleUtil.startScheduler());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ScheduleUtil.triggerJobs(ConfigHandler.ConfigHandlerAutoSaveJob.class.getName());
            ScheduleUtil.shutdownScheduler();
        });
    }
}
