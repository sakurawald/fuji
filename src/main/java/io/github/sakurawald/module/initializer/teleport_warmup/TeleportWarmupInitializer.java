package io.github.sakurawald.module.initializer.teleport_warmup;

import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.teleport_warmup.config.model.TeleportWarmupConfigModel;

public class TeleportWarmupInitializer extends ModuleInitializer {
    public final BaseConfigurationHandler<TeleportWarmupConfigModel> config = new ObjectConfigurationHandler<>(ReflectionUtil.getModuleControlFileName(this), TeleportWarmupConfigModel.class);

}
