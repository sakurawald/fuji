package io.github.sakurawald.module.bypass_things.move_speed;

import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;

import java.util.function.Supplier;

public class BypassThingsMoveSpeedModule extends AbstractModule {

    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.configWrapper.instance().modules.bypass_things.bypass_move_speed.enable;
    }
}
