package io.github.sakurawald.module.initializer.anti_build.config.model;

import java.util.HashSet;
import java.util.Set;

public class AntiBuildConfigModel {

    public Anti anti = new Anti();

    public static class Anti {
        public Break break_block = new Break();
        public Place place_block = new Place();
        public InteractItem interact_item = new InteractItem();
        public InteractBlock interact_block = new InteractBlock();
        public InteractEntity interact_entity = new InteractEntity();

        public static class Break {
            public Set<String> id = new HashSet<>() {
                {
                    this.add("minecraft:gold_block");
                }
            };
        }

        public static class Place {
            public Set<String> id = new HashSet<>() {
                {
                    this.add("minecraft:tnt");
                }
            };
        }

        public static class InteractItem {
            public Set<String> id = new HashSet<>() {
                {
                    this.add("minecraft:lava_bucket");
                }
            };
        }

        public static class InteractBlock {
            public Set<String> id = new HashSet<>() {
                {
                    this.add("minecraft:lever");
                }
            };
        }

        public static class InteractEntity {
            public Set<String> id = new HashSet<>() {
                {
                    this.add("minecraft:villager");
                }
            };

        }
    }
}
