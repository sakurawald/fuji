package fun.sakurawald.module.better_fake_player;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.ModMain;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.util.MessageUtil;
import net.fabricmc.tinyremapper.extension.mixin.common.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BetterFakePlayerModule {
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public static String getDecoratedString(final CommandContext<CommandSourceStack> context, final String name) {
        String spawnPlayerName = StringArgumentType.getString(context, name);

        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return spawnPlayerName;
        String sourcePlayerName = player.getGameProfile().getName();
        return sourcePlayerName + "_" + spawnPlayerName;
    }

    public static int getCurrentAmountLimit() {
        ArrayList<Pair<Integer, Integer>> time2limit = ConfigManager.configWrapper.instance().modules.better_fake_player.time2limit;
        LocalTime currentTime = LocalTime.now();
        int current = currentTime.getHour() * 60 + currentTime.getMinute();
        int limit = 0;
        for (Pair<Integer, Integer> pair : time2limit) {
            if (current < pair.first()) break;
            limit = pair.second();
        }
        return limit;
    }

    public static void registerScheduleTask(MinecraftServer server) {
        executorService.scheduleAtFixedRate(BetterFakePlayerModule::checkFakePlayerLimit, 0, 1, TimeUnit.MINUTES);
    }

    private static List<ServerPlayer> getFakePlayers() {
        PlayerList playerList = ModMain.SERVER.getPlayerList();
        return playerList.getPlayers().stream().filter(p -> !playerList.isWhiteListed(p.getGameProfile())).collect(Collectors.toList());
    }

    private static void checkFakePlayerLimit() {
        /* get sorted fake player list */
        List<ServerPlayer> fakePlayers = getFakePlayers();
        fakePlayers.sort((o1, o2) -> {
            String name1 = o1.getGameProfile().getName();
            String name2 = o2.getGameProfile().getName();
            return name1.compareTo(name2);
        });

        /* calculate key2players */
        HashMap<String, ArrayList<ServerPlayer>> key2players = new HashMap<>();
        for (ServerPlayer fakePlayer : fakePlayers) {
            String name = fakePlayer.getGameProfile().getName();
            int i = name.indexOf("_");
            if (i == -1) continue;
            String key = name.substring(0, i);

            key2players.putIfAbsent(key, new ArrayList<>());
            key2players.get(key).add(fakePlayer);
        }

        /* check for limits */
        int limit = getCurrentAmountLimit();
        for (String key : key2players.keySet()) {
            ArrayList<ServerPlayer> players = key2players.get(key);
            for (int i = players.size() - 1; i >= limit; i--) {
                ServerPlayer player = players.get(i);
                player.kill();
                MessageUtil.broadcast("Kick fake-player %s for limit.".formatted(player.getGameProfile().getName()), ChatFormatting.GREEN);
            }
        }

    }
}
