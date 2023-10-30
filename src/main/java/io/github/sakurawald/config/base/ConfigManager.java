package io.github.sakurawald.config.base;

import io.github.sakurawald.config.*;


public class ConfigManager {

    public static final ConfigWrapper<ConfigGSON> configWrapper = new ObjectConfigWrapper<>("config.json", ConfigGSON.class);

    public static final ConfigWrapper<ChatGSON> chatWrapper = new ObjectConfigWrapper<>("chat.json", ChatGSON.class);
    public static final ConfigWrapper<PvPGSON> pvpWrapper = new ObjectConfigWrapper<>("pvp.json", PvPGSON.class);
    public static final ConfigWrapper<WorksGSON> worksWrapper = new ObjectConfigWrapper<>("works.json", WorksGSON.class);
    public static final ConfigWrapper<HeadGSON> headWrapper = new ObjectConfigWrapper<>("head.json", HeadGSON.class);
    public static final ConfigWrapper<SchedulerGSON> schedulerWrapper = new ObjectConfigWrapper<>("scheduler.json", SchedulerGSON.class);
}
