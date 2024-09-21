package io.github.sakurawald.module.initializer.command_cooldown.config.model;

import java.util.HashMap;

public class CommandCooldownConfigModel {

    public HashMap<String, Long> regex2ms = new HashMap<>() {
        {
            this.put("rw tp (overworld|the_nether|the_end)", 120 * 1000L);
            this.put("chunks\\s*", 60 * 1000L);
            this.put("download\\s*", 120 * 1000L);
        }
    };
}
