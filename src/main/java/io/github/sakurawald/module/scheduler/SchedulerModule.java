package io.github.sakurawald.module.scheduler;

import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;

import java.util.function.Supplier;

public class SchedulerModule extends AbstractModule {

    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.configWrapper.instance().modules.scheduler.enable;
    }


}
