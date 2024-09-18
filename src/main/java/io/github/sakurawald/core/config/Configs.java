package io.github.sakurawald.core.config;

import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.config.model.ConfigModel;
import io.github.sakurawald.core.config.transformer.impl.FlattenModulesTransformer;

public class Configs {

    public static final BaseConfigurationHandler<ConfigModel> configHandler = new ObjectConfigurationHandler<>("config.json", ConfigModel.class) {
        {
            // enable detection for main-control file.
            this.detectUnknownKeysFlag = true;
        }
    }
        .addTransformer(new FlattenModulesTransformer());

}
