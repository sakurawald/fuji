package io.github.sakurawald.module.initializer.command_warmup.config.model;

import java.util.HashMap;

public class CommandWarmupConfigModel {

    public HashMap<String, Integer> regex2ms = new HashMap<>() {
        {
            this.put("back", 3 * 1000);
        }
    };
}
