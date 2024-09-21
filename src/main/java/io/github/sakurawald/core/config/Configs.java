package io.github.sakurawald.core.config;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.config.model.ConfigModel;
import io.github.sakurawald.core.config.transformer.impl.FlattenModulesTransformer;

public class Configs {

    public static final BaseConfigurationHandler<ConfigModel> configHandler = new ObjectConfigurationHandler<>(Fuji.CONFIG_PATH.resolve("config.json"), ConfigModel.class)
        .addTransformer(new FlattenModulesTransformer());

}
