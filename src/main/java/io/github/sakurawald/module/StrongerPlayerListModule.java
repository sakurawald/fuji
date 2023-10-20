package io.github.sakurawald.module;

import io.github.sakurawald.config.ConfigManager;

import java.util.function.Supplier;

public class StrongerPlayerListModule extends AbstractModule {
    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.configWrapper.instance().modules.stronger_player_list.enable;
    }
}
