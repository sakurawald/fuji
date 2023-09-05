package fun.sakurawald.module.custom_stats.registry;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fun.sakurawald.ModMain;
import fun.sakurawald.mixin.custom_stats.StatsAccessor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CustomStatisticsRegistry {
    public static Identifier MINE_ALL;
    public static Identifier PLACED_ALL;

    public static Identifier register(String name, StatFormatter statFormatter) {
        Identifier statId = new Identifier(name);
        Registry.register(Registries.CUSTOM_STAT, statId, statId);
        StatsAccessor.getCUSTOM().getOrCreateStat(statId, statFormatter);
        ModMain.LOGGER.info("Registered custom statistic " + statId);
        return statId;
    }

    public static void syncPlayersStats() {
        File file = new File("world/stats/");
        File[] files = file.listFiles();
        if (files == null) return;

        Gson gson = new Gson();
        for (File playerStatFile : files) {
            try {
                JsonObject json = JsonParser.parseReader(new FileReader(playerStatFile)).getAsJsonObject();
                JsonObject stats = json.getAsJsonObject("stats");
                if (stats == null) continue;
                int mined_all = sumUpStats(stats.getAsJsonObject("minecraft:mined"));
                int used_all = sumUpStats(stats.getAsJsonObject("minecraft:used"));

                JsonObject custom = stats.getAsJsonObject("minecraft:custom");
                custom.addProperty("minecraft:mined_all", mined_all);
                custom.addProperty("minecraft:placed_all", used_all);

                try (FileWriter writer = new FileWriter(playerStatFile)) {
                    writer.write(gson.toJson(json));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static int sumUpStats(JsonObject jsonObject) {
        if (jsonObject == null) return 0;
        int count = 0;
        for (String key : jsonObject.keySet()) {
            count += jsonObject.get(key).getAsInt();
        }
        return count;
    }
}
