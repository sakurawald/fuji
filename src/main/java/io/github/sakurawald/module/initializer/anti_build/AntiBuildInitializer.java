package io.github.sakurawald.module.initializer.anti_build;

import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.anti_build.config.model.AntiBuildConfigModel;

public class AntiBuildInitializer extends ModuleInitializer {
    public final ObjectConfigurationHandler<AntiBuildConfigModel> config = new ObjectConfigurationHandler<>(ReflectionUtil.getModuleConfigFileName(this), AntiBuildConfigModel.class);
}
