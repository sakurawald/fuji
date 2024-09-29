package io.github.sakurawald.module.initializer.command_warmup;

import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_warmup.config.model.CommandWarmupConfigModel;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CommandWarmupInitializer extends ModuleInitializer {
    public static final BaseConfigurationHandler<CommandWarmupConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, CommandWarmupConfigModel.class);

    public static int command2ms(@NotNull String commandLine) {
        for (Map.Entry<String, Integer> entry : config.getModel().regex2ms.entrySet()) {
            if (!commandLine.matches(entry.getKey())) continue;

            return entry.getValue();
        }

        return 0;
    }
}
