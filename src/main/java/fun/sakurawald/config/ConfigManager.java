package fun.sakurawald.config;

public class ConfigManager {


    /**
     * Config Instances
     **/
    public static final ConfigWrapper<ConfigGSON> configWrapper = new ConfigWrapper<>("sakurawald.json", ConfigGSON.class);

    public static final ConfigWrapper<ChatGson> chatWrapper = new ConfigWrapper<>("chat.json", ChatGson.class);

}
