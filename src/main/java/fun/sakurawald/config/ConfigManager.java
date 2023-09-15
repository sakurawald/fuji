package fun.sakurawald.config;

public class ConfigManager {


    /**
     * Config Instances
     **/
    public static final ConfigWrapper<ConfigGSON> configWrapper = new ConfigWrapper<>("config.json", ConfigGSON.class);

    public static final ConfigWrapper<ChatGSON> chatWrapper = new ConfigWrapper<>("chat.json", ChatGSON.class);

}
