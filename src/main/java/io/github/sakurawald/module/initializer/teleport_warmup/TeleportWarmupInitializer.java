package io.github.sakurawald.module.initializer.teleport_warmup;

import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.teleport_warmup.config.model.TeleportWarmupConfigModel;

public class TeleportWarmupInitializer extends ModuleInitializer {
    public final ObjectConfigurationHandler<TeleportWarmupConfigModel> config = new ObjectConfigurationHandler<>(ReflectionUtil.getModuleControlFileName(this), TeleportWarmupConfigModel.class);

}
