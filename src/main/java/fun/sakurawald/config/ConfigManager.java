package fun.sakurawald.config;

public class ConfigManager {


    /**
     * Config Instances
     **/
    public static final ConfigWrapper<ConfigGSON> configWrapper = new ConfigWrapper<>("config.json", ConfigGSON.class);

    public static final ConfigWrapper<ChatGSON> chatWrapper = new ConfigWrapper<>("chat.json", ChatGSON.class);
    public static final ConfigWrapper<PvPGSON> pvpWrapper = new ConfigWrapper<>("pvp.json", PvPGSON.class);
    public static final ConfigWrapper<WorksGSON> worksWrapper = new ConfigWrapper<>("works.json", WorksGSON.class);
    public static final ConfigWrapper<HeadGSON> headWrapper = new ConfigWrapper<>("head.json", HeadGSON.class);

    static {
        // fix: use static-initialize-block to prevent NPE
        loadConfigsFromDisk();
    }

    public static void loadConfigsFromDisk() {
        ConfigManager.configWrapper.loadFromDisk();
        ConfigManager.chatWrapper.loadFromDisk();
        ConfigManager.pvpWrapper.loadFromDisk();
        ConfigManager.worksWrapper.loadFromDisk();
        ConfigManager.headWrapper.loadFromDisk();
    }
}
