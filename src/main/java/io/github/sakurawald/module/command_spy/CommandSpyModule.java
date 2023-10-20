package io.github.sakurawald.module.command_spy;

import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;

import java.util.function.Supplier;

public class CommandSpyModule extends AbstractModule {

    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.configWrapper.instance().modules.command_spy.enable;
    }
}
