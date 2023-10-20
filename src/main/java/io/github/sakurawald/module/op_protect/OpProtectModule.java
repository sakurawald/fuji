package io.github.sakurawald.module.op_protect;

import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;

import java.util.function.Supplier;

public class OpProtectModule extends AbstractModule {
    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.configWrapper.instance().modules.op_protect.enable;
    }
}
