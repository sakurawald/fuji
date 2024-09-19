package io.github.sakurawald.module.initializer.teleport_warmup.config.model;

import java.util.HashSet;
import java.util.Set;

public class TeleportWarmupConfigModel {

    public int warmup_second = 3;

    public double interrupt_distance = 1d;

    public Dimension dimension = new Dimension();

    public static class Dimension {
        public Set<String> list = new HashSet<>() {
            {
                this.add("minecraft:overworld");
                this.add("minecraft:the_nether");
                this.add("minecraft:the_end");
            }
        };
    }
}
