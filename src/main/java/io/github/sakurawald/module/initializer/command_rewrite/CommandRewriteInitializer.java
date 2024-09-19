package io.github.sakurawald.module.initializer.command_rewrite;

import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_rewrite.config.model.CommandRewriteConfigModel;

public class CommandRewriteInitializer extends ModuleInitializer {
    public final ObjectConfigurationHandler<CommandRewriteConfigModel> config = new ObjectConfigurationHandler<>(ReflectionUtil.getModuleControlFileName(this), CommandRewriteConfigModel.class);
}
