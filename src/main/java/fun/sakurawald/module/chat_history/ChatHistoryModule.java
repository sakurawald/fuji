package fun.sakurawald.module.chat_history;

import com.google.common.collect.EvictingQueue;
import fun.sakurawald.config.ConfigManager;
import net.kyori.adventure.text.Component;

import java.util.Queue;

public class ChatHistoryModule {
    public static Queue<Component> CACHE = EvictingQueue.create(ConfigManager.configWrapper.instance().modules.chat_history.max_history);

}
