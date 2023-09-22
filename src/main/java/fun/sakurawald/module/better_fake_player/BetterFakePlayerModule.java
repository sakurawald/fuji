package fun.sakurawald.module.better_fake_player;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.ServerMain;
import fun.sakurawald.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static fun.sakurawald.util.MessageUtil.ofComponent;
import static fun.sakurawald.util.MessageUtil.sendBroadcast;

public class BetterFakePlayerModule {
    private static final HashMap<String, ArrayList<String>> player2fakePlayers = new HashMap<>();

    @SuppressWarnings("unused")
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("player").then(
                        Commands.literal("who").executes(BetterFakePlayerModule::$who)
                )
        );
    }

    private static int $who(CommandContext<CommandSourceStack> context) {
        /* validate */
        validateFakePlayers();

        /* output */
        StringBuilder builder = new StringBuilder();
        for (String player : player2fakePlayers.keySet()) {
            builder.append(player).append(": ");
            for (String fakePlayer : player2fakePlayers.get(player)) {
                builder.append(fakePlayer).append(" ");
            }
            builder.append("\n");
        }
        CommandSourceStack source = context.getSource();
        source.sendMessage(ofComponent(source, "better_fake_player.who.header").append(Component.text(builder.toString())));
        return Command.SINGLE_SUCCESS;
    }

    private static void validateFakePlayers() {
        for (Map.Entry<String, ArrayList<String>> entry : player2fakePlayers.entrySet()) {
            ArrayList<String> myFakePlayers = entry.getValue();
            // fix: NPE
            if (myFakePlayers == null || myFakePlayers.isEmpty()) {
                player2fakePlayers.remove(entry.getKey());
                continue;
            }
            myFakePlayers.removeIf(name -> {
                ServerPlayer fakePlayer = ServerMain.SERVER.getPlayerList().getPlayerByName(name);
                return fakePlayer == null || fakePlayer.isRemoved();
            });
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

    public static boolean canManipulateFakePlayer(CommandContext<CommandSourceStack> ctx, String fakePlayer) {
        // IMPORTANT: disable /player ... shadow command for online-player
        if (ctx.getNodes().get(2).getNode().getName().equals("shadow")) return false;

        // bypass: console
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return true;

        // bypass: op
        if (ServerMain.SERVER.getPlayerList().isOp(player.getGameProfile())) return true;

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

    @SuppressWarnings("unused")
    public static void registerScheduleTask(MinecraftServer server) {
        ServerMain.getScheduledExecutor().scheduleAtFixedRate(BetterFakePlayerModule::checkFakePlayerLimit, 0, 1, TimeUnit.MINUTES);
    }

    public static boolean isFakePlayer(ServerPlayer player) {
        return player.getClass() != ServerPlayer.class;
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
                ServerPlayer fakePlayer = ServerMain.SERVER.getPlayerList().getPlayerByName(fakePlayers.get(i));
                if (fakePlayer == null) continue;
                fakePlayer.kill();

                sendBroadcast("better_fake_player.kick_for_limit", fakePlayer.getGameProfile().getName());
            }
        }
    }
}
