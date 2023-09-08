package fun.sakurawald.module.custom_stats;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fun.sakurawald.ModMain;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.mixin.custom_stats.StatsAccessor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CustomStatisticsModule {
    private static final int CM_TO_KM_DIVISOR = 100 * 1000;
    private static final int GT_TO_H_DIVISOR = 20 * 3600;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    public static Identifier MINE_ALL;
    public static Identifier PLACED_ALL;
    public static Identifier MOVED_ALL;

    public static void registerCustomStats() {
        CustomStatisticsModule.MINE_ALL = CustomStatisticsModule.registerCustomStat("mined_all", StatFormatter.DEFAULT);
        CustomStatisticsModule.PLACED_ALL = CustomStatisticsModule.registerCustomStat("placed_all", StatFormatter.DEFAULT);
        CustomStatisticsModule.MOVED_ALL = CustomStatisticsModule.registerCustomStat("moved_all", StatFormatter.DEFAULT);
    }

    private static Identifier registerCustomStat(String name, StatFormatter statFormatter) {
        Identifier statId = new Identifier(name);
        Registry.register(Registries.CUSTOM_STAT, name, statId);
        StatsAccessor.getCUSTOM().getOrCreateStat(statId, statFormatter);
        ModMain.LOGGER.info("Registered custom statistic " + statId);
        return statId;
    }

    public static ServerStats mergeServerStats() {
        ServerStats ret = new ServerStats();

        File file = new File("world/stats/");
        File[] files = file.listFiles();
        if (files == null) return ret;

        Gson gson = new Gson();
        for (File playerStatFile : files) {
            try {
                // get player statistics
                JsonObject json = JsonParser.parseReader(new FileReader(playerStatFile)).getAsJsonObject();
                JsonObject stats = json.getAsJsonObject("stats");
                if (stats == null) continue;
                int mined_all = sumUpStats(stats.getAsJsonObject("minecraft:mined"), ".*");
                int used_all = sumUpStats(stats.getAsJsonObject("minecraft:used"), ".*");
                int moved_all = sumUpStats(stats.getAsJsonObject("minecraft:custom"), ".+_cm") / CM_TO_KM_DIVISOR;
                JsonElement mobKills = stats.getAsJsonObject("minecraft:custom").get("minecraft:mob_kills");
                int mob_kills = (mobKills == null ? 0 : mobKills.getAsInt());
                JsonElement playTime = stats.getAsJsonObject("minecraft:custom").get("minecraft:play_time");
                int play_time = (playTime == null ? 0 : playTime.getAsInt()) / GT_TO_H_DIVISOR;

                // accumulate server statistics
                ret.server_playtime += play_time;
                ret.server_mined += mined_all;
                ret.server_placed += used_all;
                ret.server_killed += mob_kills;
                ret.server_moved += moved_all;

                // sync player stat file
                JsonObject custom = stats.getAsJsonObject("minecraft:custom");
                custom.addProperty(MINE_ALL.toString(), mined_all);
                custom.addProperty(PLACED_ALL.toString(), used_all);
                custom.addProperty(MOVED_ALL.toString(), moved_all);

                try (FileWriter writer = new FileWriter(playerStatFile)) {
                    writer.write(gson.toJson(json));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return ret;
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

    private static void updateMovedAllStat(ServerPlayerEntity player) {
        ServerStatHandler statHandler = player.getStatHandler();
        int value =
                (statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.WALK_ONE_CM))
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
        statHandler.setStat(player, Stats.CUSTOM.getOrCreateStat(MOVED_ALL), value);
    }

    public static void updateMOTD() {
        ServerStats serverStats = mergeServerStats();
        String motd = ConfigManager.configWrapper.instance().modules.custom_stats.dynamic_motd
                .replace("%server_playtime%", String.valueOf(serverStats.server_playtime))
                .replace("%server_mined%", String.valueOf(serverStats.server_mined))
                .replace("%server_placed%", String.valueOf(serverStats.server_placed))
                .replace("%server_killed%", String.valueOf(serverStats.server_killed))
                .replace("%server_moved%", String.valueOf(serverStats.server_moved));
        ModMain.SERVER.setMotd(motd);
    }

    public static void registerScheduleTask(MinecraftServer server) {
        // async task
        executorService.scheduleAtFixedRate(() -> {
            // update all players' moved_all stat and save its stat data into disk
            server.getPlayerManager().getPlayerList().forEach((p) -> {
                updateMovedAllStat(p);
                p.getStatHandler().save();
            });

            // update motd
            updateMOTD();
        }, 60, 60, TimeUnit.SECONDS);
    }

    private static class ServerStats {
        public int server_playtime;
        public int server_mined;
        public int server_placed;
        public int server_killed;
        public int server_moved;
    }
}
