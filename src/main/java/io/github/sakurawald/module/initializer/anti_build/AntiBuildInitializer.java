package io.github.sakurawald.module.initializer.anti_build;

import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.anti_build.config.model.AntiBuildConfigModel;

public class AntiBuildInitializer extends ModuleInitializer {
    public static final BaseConfigurationHandler<AntiBuildConfigModel> config = new ObjectConfigurationHandler<>("config.json", AntiBuildConfigModel.class);
}
