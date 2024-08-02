package io.github.sakurawald.module.initializer.chat.history;

import com.google.common.collect.EvictingQueue;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minecraft.text.Text;

import javax.naming.OperationNotSupportedException;
import java.util.Queue;

@SuppressWarnings("LombokGetterMayBeUsed")
public class ChatHistoryInitializer extends ModuleInitializer {

    @Getter
    private Queue<Text> chatHistory;

    @Override
    public void onInitialize() {
        chatHistory = EvictingQueue.create(Configs.configHandler.model().modules.chat.history.buffer_size);
    }

    @Override
    public void onReload() {
        EvictingQueue<Text> newQueue = EvictingQueue.create(Configs.configHandler.model().modules.chat.history.buffer_size);
        newQueue.addAll(chatHistory);
        chatHistory.clear();
        chatHistory = newQueue;
    }
}

