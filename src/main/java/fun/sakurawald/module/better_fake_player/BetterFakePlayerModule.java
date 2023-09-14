package fun.sakurawald.module.better_fake_player;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fun.sakurawald.ModMain;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BetterFakePlayerModule {
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static final HashMap<String, ArrayList<String>> player2fakePlayers = new HashMap<>();

    public static LiteralCommandNode<CommandSourceStack> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        return dispatcher.register(
                Commands.literal("player").then(
                        Commands.literal("who").executes(BetterFakePlayerModule::who)
                )
        );
    }

    private static int who(CommandContext<CommandSourceStack> context) {
        /* validate */
        validateFakePlayers();

        /* output */
        StringBuilder builder = new StringBuilder();
        builder.append("--- Fake Players ---\n");
        for (String player : player2fakePlayers.keySet()) {
            builder.append(player).append(": ");
            for (String fakePlayer : player2fakePlayers.get(player)) {
                builder.append(fakePlayer).append(" ");
            }
            builder.append("\n");
        }
        MessageUtil.feedback(context.getSource(), builder.toString());
        return 1;
    }

    private static void validateFakePlayers() {
        for (Map.Entry<String, ArrayList<String>> entry : player2fakePlayers.entrySet()) {
            ArrayList<String> fakePlayers = entry.getValue();
            fakePlayers.removeIf(name -> {
                ServerPlayer fakePlayer = ModMain.SERVER.getPlayerList().getPlayerByName(name);
                return fakePlayer == null || fakePlayer.isRemoved();
            });
            if (fakePlayers.isEmpty()) {
                player2fakePlayers.remove(entry.getKey());
            }
        }
    }

    public static boolean canSpawnFakePlayer(ServerPlayer player) {
        /* validate */
        validateFakePlayers();

        /* check */
        int limit = BetterFakePlayerModule.getCurrentAmountLimit();
        int current = BetterFakePlayerModule.player2fakePlayers.getOrDefault(player.getGameProfile().getName(), new ArrayList<>()).size();
        return current < limit;
    }

    public static void addFakePlayer(ServerPlayer player, String fakePlayer) {
        BetterFakePlayerModule.player2fakePlayers.computeIfAbsent(player.getGameProfile().getName(), k -> new ArrayList<>()).add(fakePlayer);
    }

    public static boolean canManipulateFakePlayer(ServerPlayer player, String fakePlayer) {
        // skip check for op
        if (ModMain.SERVER.getPlayerList().isOp(player.getGameProfile())) return true;

        ArrayList<String> myFakePlayers = BetterFakePlayerModule.player2fakePlayers.getOrDefault(player.getGameProfile().getName(), new ArrayList<>());
        return myFakePlayers.contains(fakePlayer);
    }

    private static int getCurrentAmountLimit() {
        ArrayList<List<Integer>> rules = ConfigManager.configWrapper.instance().modules.better_fake_player.limit_rule;
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        int currentDays = currentDate.getDayOfWeek().getValue();
        int currentMinutes = currentTime.getHour() * 60 + currentTime.getMinute();
        for (List<Integer> rule : rules) {
            if (currentDays >= rule.get(0) && currentMinutes >= rule.get(1)) return rule.get(2);
        }
        return -1;
    }

    public static void registerScheduleTask(MinecraftServer server) {
        executorService.scheduleAtFixedRate(BetterFakePlayerModule::checkFakePlayerLimit, 0, 1, TimeUnit.MINUTES);
    }

    public static boolean isFakePlayer(ServerPlayer player) {
        return !ModMain.SERVER.getPlayerList().isWhiteListed(player.getGameProfile())
                || player2fakePlayers.values().stream().anyMatch(fakePlayers -> fakePlayers.contains(player.getGameProfile().getName()));
    }

    public static GameProfile createOfflineGameProfile(String fakePlayerName) {
        UUID offlinePlayerUUID = UUIDUtil.createOfflinePlayerUUID(fakePlayerName);
        return new GameProfile(offlinePlayerUUID, fakePlayerName);
    }

    private static void checkFakePlayerLimit() {
        /* validate */
        validateFakePlayers();

        /* check for limits */
        int limit = getCurrentAmountLimit();
        for (String player : player2fakePlayers.keySet()) {
            ArrayList<String> fakePlayers = player2fakePlayers.getOrDefault(player, new ArrayList<>());
            for (int i = fakePlayers.size() - 1; i >= limit; i--) {
                ServerPlayer fakePlayer = ModMain.SERVER.getPlayerList().getPlayerByName(fakePlayers.get(i));
                if (fakePlayer == null) continue;
                fakePlayer.kill();
                MessageUtil.broadcast("Kick fake-player %s for limit.".formatted(fakePlayer.getGameProfile().getName()), ChatFormatting.GREEN);
            }
        }
    }
}
