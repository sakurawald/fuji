package fun.sakurawald.module.chat_history;

import com.google.common.collect.EvictingQueue;
import fun.sakurawald.config.ConfigManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Queue;

@SuppressWarnings("UnstableApiUsage")
public class CachedMessage {
    public static Queue<CachedMessage> MESSAGE_CACHE = EvictingQueue.create(ConfigManager.configWrapper.instance().modules.chat_history.max_history);

    private final Text message;

    public CachedMessage(Text message) {
        this.message = message;
    }

    public void send(ServerPlayerEntity player) {
        player.sendMessage(message, false);
    }
}
