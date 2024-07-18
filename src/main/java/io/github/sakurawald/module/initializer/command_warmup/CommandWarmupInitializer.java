package io.github.sakurawald.module.initializer.command_warmup;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;

import java.util.Map;

public class CommandWarmupInitializer extends ModuleInitializer {

    public long getMs(String commandLine) {
        for (Map.Entry<String, Long> entry : Configs.configHandler.model().modules.command_warmup.regex2ms.entrySet()) {
            if (!commandLine.matches(entry.getKey())) continue;
            return entry.getValue();
        }

        return 0;
    }
}
