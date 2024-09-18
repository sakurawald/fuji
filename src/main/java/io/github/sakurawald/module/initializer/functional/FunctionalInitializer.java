package io.github.sakurawald.module.initializer.functional;

import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.functional.config.model.FunctionalConfigModel;

public class FunctionalInitializer extends ModuleInitializer {

    public final ObjectConfigurationHandler<FunctionalConfigModel> config = new ObjectConfigurationHandler<>(getModuleConfigFileName(), FunctionalConfigModel.class);

    @Override
    public void onInitialize() {
        config.readStorage();
    }

    @Override
    public void onReload() {
        config.readStorage();
    }
}
