package io.github.sakurawald.module.initializer.cleaner.config.model;

import java.util.HashMap;
import java.util.Map;

public class CleanerConfigModel {

    public String cron = "0 * * ? * * *";

    public Map<String, Integer> key2age = new HashMap<>() {
        {
            this.put("block.minecraft.sand", 1200);
            this.put("item.minecraft.ender_pearl", 1200);
            this.put("block.minecraft.white_carpet", 1200);
            this.put("block.minecraft.cobblestone", 1200);
        }
    };

    public Ignore ignore = new Ignore();

    public static class Ignore {
        public boolean ignore_item_entity = false;
        public boolean ignore_living_entity = true;
        public boolean ignore_named_entity = true;
        public boolean ignore_entity_with_vehicle = true;
        public boolean ignore_entity_with_passengers = true;
        public boolean ignore_glowing_entity = true;
        public boolean ignore_leashed_entity = true;
    }

}
