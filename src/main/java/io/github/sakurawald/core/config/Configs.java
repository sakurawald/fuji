package io.github.sakurawald.core.config;

import io.github.sakurawald.core.config.handler.abst.ConfigHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigHandler;
import io.github.sakurawald.core.config.model.ConfigModel;


public class Configs {

    public static final ConfigHandler<ConfigModel> configHandler = new ObjectConfigHandler<>("config.json", ConfigModel.class);

}
