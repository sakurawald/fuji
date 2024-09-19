package io.github.sakurawald.module.initializer.command_warmup;

import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_warmup.config.model.CommandWarmupConfigModel;

public class CommandWarmupInitializer extends ModuleInitializer {
    public final ObjectConfigurationHandler<CommandWarmupConfigModel> config = new ObjectConfigurationHandler<>(ReflectionUtil.getModuleControlFileName(this), CommandWarmupConfigModel.class);
}
