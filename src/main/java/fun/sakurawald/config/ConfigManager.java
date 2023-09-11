package fun.sakurawald.config;

import fun.sakurawald.config.configs.ConfigGSON;

public class ConfigManager {


    /**
     * Config Instances
     **/
    public static final ConfigWrapper<ConfigGSON> configWrapper = new ConfigWrapper<>("sakurawald.json", ConfigGSON.class);

}
