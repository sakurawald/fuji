package io.github.sakurawald.module.initializer.placeholder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
public class PlayerSumUpPlaceholder {
    public static final HashMap<String, PlayerSumUpPlaceholder> uuid2stats = new HashMap<>();
    private static final int CM_TO_KM_DIVISOR = 100 * 1000;
    private static final int GT_TO_H_DIVISOR = 20 * 3600;

    @Getter
    private static PlayerSumUpPlaceholder ofServer;

    public int playtime;
    public int mined;
    public int placed;
    public int killed;
    public int moved;

    public static PlayerSumUpPlaceholder ofPlayer(String uuid) {
       if (uuid2stats.containsKey(uuid)) {
           return uuid2stats.get(uuid);
       }
        
        PlayerSumUpPlaceholder ret = new PlayerSumUpPlaceholder();
        
        try {
            // get player statistics
            File file = new File("world/stats/" + uuid + ".json");
            if (!file.exists()) return ret;

            JsonObject json = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
            JsonObject stats = json.getAsJsonObject("stats");
            if (stats == null) return ret;

            int mined_all = sumUpStats(stats.getAsJsonObject("minecraft:mined"), ".*");
            int used_all = sumUpStats(stats.getAsJsonObject("minecraft:used"), ".*");
            JsonObject custom = stats.getAsJsonObject("minecraft:custom");
            if (custom == null) return ret;
            int moved_all = sumUpStats(custom, ".+_cm") / CM_TO_KM_DIVISOR;
            JsonElement mobKills = custom.get("minecraft:mob_kills");
            int mob_kills = (mobKills == null ? 0 : mobKills.getAsInt());
            JsonElement playTime = custom.get("minecraft:play_time");
            int play_time = (playTime == null ? 0 : playTime.getAsInt()) / GT_TO_H_DIVISOR;

            // set main-stats
            ret.playtime = play_time;
            ret.mined = mined_all;
            ret.placed = used_all;
            ret.killed = mob_kills;
            ret.moved = moved_all;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // save
        uuid2stats.put(uuid, ret);
        return ret;
    }

    public static PlayerSumUpPlaceholder ofServer() {
        PlayerSumUpPlaceholder ofServer = new PlayerSumUpPlaceholder();
        File file = new File("world/stats/");
        File[] files = file.listFiles();
        if (files == null) return ofServer;

        for (File playerStatFile : files) {
            String uuid = playerStatFile.getName().replace(".json", "");
            PlayerSumUpPlaceholder playerMainStats = ofPlayer(uuid);
            ofServer.add(playerMainStats);
        }

        // save
        PlayerSumUpPlaceholder.ofServer = ofServer;

        return ofServer;
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

    public PlayerSumUpPlaceholder updatePlayer(ServerPlayerEntity player) {
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

    public void add(PlayerSumUpPlaceholder other) {
        this.playtime += other.playtime;
        this.mined += other.mined;
        this.placed += other.placed;
        this.killed += other.killed;
        this.moved += other.moved;
    }
}
