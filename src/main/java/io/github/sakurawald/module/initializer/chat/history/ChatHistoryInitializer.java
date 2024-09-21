package io.github.sakurawald.module.initializer.chat.history;

import com.google.common.collect.EvictingQueue;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.chat.history.config.model.ChatHistoryConfigModel;
import lombok.Getter;
import net.minecraft.text.Text;

import java.util.Queue;

@SuppressWarnings("LombokGetterMayBeUsed")
public class ChatHistoryInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<ChatHistoryConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, ChatHistoryConfigModel.class);

    @Getter
    private Queue<Text> chatHistory;

    @Override
    public void onInitialize() {
        chatHistory = EvictingQueue.create(config.getModel().buffer_size);
    }

    @Override
    public void onReload() {
        EvictingQueue<Text> newQueue = EvictingQueue.create(config.getModel().buffer_size);
        newQueue.addAll(chatHistory);
        chatHistory.clear();
        chatHistory = newQueue;
    }
}

