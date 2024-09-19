package io.github.sakurawald.module.initializer.tab_list.faker;

import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.tab_list.faker.config.model.TabListFakerConfigModel;

public class TabListFakerInitializer extends ModuleInitializer {

    public final ObjectConfigurationHandler<TabListFakerConfigModel> config = new ObjectConfigurationHandler<>(ReflectionUtil.getModuleConfigFileName(this), TabListFakerConfigModel.class);

}
