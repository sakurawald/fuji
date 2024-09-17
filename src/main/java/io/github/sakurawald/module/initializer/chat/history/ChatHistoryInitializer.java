package io.github.sakurawald.module.initializer.chat.history;

import com.google.common.collect.EvictingQueue;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.Getter;
import net.minecraft.text.Text;

import java.util.Queue;

@SuppressWarnings("LombokGetterMayBeUsed")
public class ChatHistoryInitializer extends ModuleInitializer {

    @Getter
    private Queue<Text> chatHistory;

    @Override
    public void onInitialize() {
        chatHistory = EvictingQueue.create(Configs.configHandler.getModel().modules.chat.history.buffer_size);
    }

    @Override
    public void onReload() {
        EvictingQueue<Text> newQueue = EvictingQueue.create(Configs.configHandler.getModel().modules.chat.history.buffer_size);
        newQueue.addAll(chatHistory);
        chatHistory.clear();
        chatHistory = newQueue;
    }
}

