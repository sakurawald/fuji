package io.github.sakurawald.config;

import io.github.sakurawald.config.handler.abst.ConfigHandler;
import io.github.sakurawald.config.handler.impl.ObjectConfigHandler;
import io.github.sakurawald.config.model.*;


public class Configs {

    public static final ConfigHandler<ConfigModel> configHandler = new ObjectConfigHandler<>("config.json", ConfigModel.class);

}
