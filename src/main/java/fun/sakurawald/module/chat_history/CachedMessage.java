package fun.sakurawald.module.chat_history;

import com.google.common.collect.EvictingQueue;
import fun.sakurawald.config.ConfigManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Queue;

@SuppressWarnings("UnstableApiUsage")
public class CachedMessage {
    public static Queue<CachedMessage> MESSAGE_CACHE = EvictingQueue.create(ConfigManager.configWrapper.instance().modules.chat_history.max_history);

    private final Component message;

    public CachedMessage(Component message) {
        this.message = message;
    }

    public void send(ServerPlayer player) {
        player.displayClientMessage(message, false);
    }
}
