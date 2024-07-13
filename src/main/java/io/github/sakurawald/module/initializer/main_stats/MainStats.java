package io.github.sakurawald.module.initializer.main_stats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.ToString;
import net.fabricmc.loader.language.LanguageAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

@ToString
public class MainStats {
    public static final HashMap<String, MainStats> uuid2stats = new HashMap<>();
    private static final int CM_TO_KM_DIVISOR = 100 * 1000;
    private static final int GT_TO_H_DIVISOR = 20 * 3600;
    public int playtime;
    public int mined;
    public int placed;
    public int killed;
    public int moved;

    public static MainStats calculatePlayerMainStats(String uuid) {
        MainStats playerMainStats = new MainStats();
        try {
            // get player statistics
            File file = new File("world/stats/" + uuid + ".json");
            if (!file.exists()) return playerMainStats;
            JsonObject json = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
            JsonObject stats = json.getAsJsonObject("stats");
            if (stats == null) return playerMainStats;
            int mined_all = sumUpStats(stats.getAsJsonObject("minecraft:mined"), ".*");
            int used_all = sumUpStats(stats.getAsJsonObject("minecraft:used"), ".*");
            JsonObject custom = stats.getAsJsonObject("minecraft:custom");
            if (custom == null) return playerMainStats;
            int moved_all = sumUpStats(custom, ".+_cm") / CM_TO_KM_DIVISOR;
            JsonElement mobKills = custom.get("minecraft:mob_kills");
            int mob_kills = (mobKills == null ? 0 : mobKills.getAsInt());
            JsonElement playTime = custom.get("minecraft:play_time");
            int play_time = (playTime == null ? 0 : playTime.getAsInt()) / GT_TO_H_DIVISOR;

            // set main-stats
            playerMainStats.playtime += play_time;
            playerMainStats.mined += mined_all;
            playerMainStats.placed += used_all;
            playerMainStats.killed += mob_kills;
            playerMainStats.moved += moved_all;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return playerMainStats;
    }

    public static MainStats calculateServerMainStats() {
        MainStats serverMainStats = new MainStats();
        File file = new File("world/stats/");
        File[] files = file.listFiles();
        if (files == null) return serverMainStats;

        for (File playerStatFile : files) {
            String uuid = playerStatFile.getName().replace(".json", "");
            MainStats playerMainStats = MainStats.calculatePlayerMainStats(uuid);
            serverMainStats.add(playerMainStats);
        }

        return serverMainStats;
    }

    private static int sumUpStats(JsonObject jsonObject, String regex) {
        if (jsonObject == null) return 0;
        int count = 0;
        Pattern pattern = Pattern.compile(regex);
        for (String key : jsonObject.keySet()) {
            if (pattern.matcher(key).matches()) {
                count += jsonObject.get(key).getAsInt();
            }
        }
        return count;
    }

    public MainStats update(ServerPlayerEntity player) {
        this.playtime = player.getStatHandler().getStat((Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME))) / GT_TO_H_DIVISOR;
        this.killed = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.MOB_KILLS));
        ServerStatHandler statHandler = player.getStatHandler();
        this.moved = (statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.WALK_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.CROUCH_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.SPRINT_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.WALK_ON_WATER_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.FALL_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.CLIMB_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.FLY_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.WALK_UNDER_WATER_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.MINECART_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.BOAT_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.PIG_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.HORSE_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.AVIATE_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.SWIM_ONE_CM))
                + statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.STRIDER_ONE_CM))) / CM_TO_KM_DIVISOR;
        return this;
    }

    public String resolve(MinecraftServer server, String str) {
        return str.replace("%playtime%", String.valueOf(playtime))
                .replace("%mined%", String.valueOf(mined))
                .replace("%placed%", String.valueOf(placed))
                .replace("%killed%", String.valueOf(killed))
                .replace("%moved%", String.valueOf(moved))
                .replace("%uptime%", String.valueOf(server.getTicks() / GT_TO_H_DIVISOR))
                .replace("%version%", server.getVersion());
    }

    public void add(MainStats other) {
        this.playtime += other.playtime;
        this.mined += other.mined;
        this.placed += other.placed;
        this.killed += other.killed;
        this.moved += other.moved;
    }
}
