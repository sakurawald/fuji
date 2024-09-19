package io.github.sakurawald.module.initializer.system_message;

import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.system_message.config.model.SystemMessageConfigModel;

public class SystemMessageInitializer extends ModuleInitializer {

    public final ObjectConfigurationHandler<SystemMessageConfigModel> config = new ObjectConfigurationHandler<>(ReflectionUtil.getModuleConfigFileName(this), SystemMessageConfigModel.class);

}
