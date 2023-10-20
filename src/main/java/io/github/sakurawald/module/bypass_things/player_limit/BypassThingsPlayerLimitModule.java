package io.github.sakurawald.module.bypass_things.player_limit;

import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;

import java.util.function.Supplier;

public class BypassThingsPlayerLimitModule extends AbstractModule {
    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.configWrapper.instance().modules.bypass_things.bypass_player_limit.enable;
    }
}
