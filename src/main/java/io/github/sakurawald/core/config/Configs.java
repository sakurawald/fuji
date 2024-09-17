package io.github.sakurawald.core.config;

import io.github.sakurawald.core.config.handler.abst.ConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.config.model.ConfigModel;


public class Configs {

    public static final ConfigurationHandler<ConfigModel> configHandler = new ObjectConfigurationHandler<>("config.json", ConfigModel.class);

}
