package io.github.sakurawald.module.initializer.command_spy;

import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_spy.config.model.CommandSpyConfigModel;

public class CommandSpyInitializer extends ModuleInitializer {
    public static final BaseConfigurationHandler<CommandSpyConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, CommandSpyConfigModel.class);
}
