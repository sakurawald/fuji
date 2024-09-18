package io.github.sakurawald.module.initializer.anti_build;

import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.anti_build.config.model.AntiBuildConfigModel;

public class AntiBuildInitializer extends ModuleInitializer {
    public final ObjectConfigurationHandler<AntiBuildConfigModel> config = new ObjectConfigurationHandler<>(getModuleConfigFileName(), AntiBuildConfigModel.class);

    @Override
    public void onInitialize() {
        config.readStorage();
    }

    @Override
    public void onReload() {
        config.readStorage();
    }
}
