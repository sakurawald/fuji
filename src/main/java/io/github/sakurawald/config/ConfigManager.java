package io.github.sakurawald.config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigManager {

    public static final ConfigWrapper<ConfigGSON> configWrapper = new ConfigWrapper<>("config.json", ConfigGSON.class);

    public static final ConfigWrapper<ChatGSON> chatWrapper = new ConfigWrapper<>("chat.json", ChatGSON.class);
    public static final ConfigWrapper<PvPGSON> pvpWrapper = new ConfigWrapper<>("pvp.json", PvPGSON.class);
    public static final ConfigWrapper<WorksGSON> worksWrapper = new ConfigWrapper<>("works.json", WorksGSON.class);
    public static final ConfigWrapper<HeadGSON> headWrapper = new ConfigWrapper<>("head.json", HeadGSON.class);

    public static final ConfigWrapper<OptimizationGSON> optimizationWrapper = new ConfigWrapper<>("optimization.json", OptimizationGSON.class);

    public static final ConfigWrapper<SchedulerGSON> schedulerWrapper = new ConfigWrapper<>("scheduler.json", SchedulerGSON.class);
}
