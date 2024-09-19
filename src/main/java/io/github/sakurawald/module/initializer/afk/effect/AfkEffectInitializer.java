package io.github.sakurawald.module.initializer.afk.effect;

import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.afk.effect.config.model.AfkEffectConfigModel;

public class AfkEffectInitializer extends ModuleInitializer {

    public final ObjectConfigurationHandler<AfkEffectConfigModel> config = new ObjectConfigurationHandler<>(getModuleConfigFileName(), AfkEffectConfigModel.class);
}
