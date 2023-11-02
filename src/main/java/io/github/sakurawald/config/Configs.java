package io.github.sakurawald.config;

import io.github.sakurawald.config.model.*;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;


public class Configs {

    public static final ConfigHandler<ConfigModel> configHandler = new ObjectConfigHandler<>("config.json", ConfigModel.class);
    public static final ConfigHandler<ChatModel> chatHandler = new ObjectConfigHandler<>("chat.json", ChatModel.class);
    public static final ConfigHandler<PvPModel> pvpHandler = new ObjectConfigHandler<>("pvp.json", PvPModel.class);
    public static final ConfigHandler<WorksModel> worksHandler = new ObjectConfigHandler<>("works.json", WorksModel.class);
    public static final ConfigHandler<HeadModel> headHandler = new ObjectConfigHandler<>("head.json", HeadModel.class);
    public static final ConfigHandler<SchedulerModule> schedulerHandler = new ObjectConfigHandler<>("scheduler.json", SchedulerModule.class);
}
