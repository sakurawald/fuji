package io.github.sakurawald.module.initializer.gameplay.carpet.fake_player_manager;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.gameplay.carpet.fake_player_manager.job.ManageFakePlayersJob;
import io.github.sakurawald.util.DateUtil;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.text.Component;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class FakePlayerManagerInitializer extends ModuleInitializer {
    public final ArrayList<String> CONSTANT_EMPTY_LIST = new ArrayList<>();
    public final HashMap<String, ArrayList<String>> player2fakePlayers = new HashMap<>();
    public final HashMap<String, Long> player2expiration = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> new ManageFakePlayersJob().schedule());
    }

    @Command("player renew")
    private int $renew(@CommandSource ServerPlayerEntity player) {
        renewFakePlayers(player);
        return CommandHelper.Return.SUCCESS;
    }


    @Command("player who")
    private int $who(@CommandSource CommandContext<ServerCommandSource> context) {
        /* validate */
        validateFakePlayers();

        /* output */
        StringBuilder builder = new StringBuilder();
        for (String player : player2fakePlayers.keySet()) {
            ArrayList<String> fakePlayers = player2fakePlayers.get(player);
            if (fakePlayers.isEmpty()) continue;
            builder.append(player).append(": ");
            for (String fakePlayer : fakePlayers) {
                builder.append(fakePlayer).append(" ");
            }
            builder.append("\n");
        }
        ServerCommandSource source = context.getSource();
        source.sendMessage(MessageHelper.ofComponent(source, "fake_player_manager.who.header").append(Component.text(builder.toString())));
        return CommandHelper.Return.SUCCESS;
    }

    public boolean hasFakePlayers(@NotNull ServerPlayerEntity player) {
        validateFakePlayers();
        return player2fakePlayers.containsKey(player.getGameProfile().getName());
    }

    public void renewFakePlayers(@NotNull ServerPlayerEntity player) {
        String name = player.getGameProfile().getName();
        int duration = Configs.configHandler.model().modules.gameplay.carpet.fake_player_manager.renew_duration_ms;
        long newTime = System.currentTimeMillis() + duration;
        player2expiration.put(name, newTime);
        MessageHelper.sendMessage(player, "fake_player_manager.renew.success", DateUtil.toStandardDateFormat(newTime));
    }

    public void validateFakePlayers() {
        /* remove invalid fake-player */
        Iterator<Map.Entry<String, ArrayList<String>>> it = player2fakePlayers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<String>> entry = it.next();

            ArrayList<String> myFakePlayers = entry.getValue();
            // fix: NPE
            if (myFakePlayers == null || myFakePlayers.isEmpty()) {
                it.remove();
                continue;
            }
            myFakePlayers.removeIf(name -> {
                ServerPlayerEntity fakePlayer = ServerHelper.getDefaultServer().getPlayerManager().getPlayer(name);
                return fakePlayer == null || fakePlayer.isRemoved();
            });
        }
    }

    public boolean canSpawnFakePlayer(@NotNull ServerPlayerEntity player) {
        /* validate */
        validateFakePlayers();

        /* check */
        int limit = this.getCurrentAmountLimit();
        int current = this.player2fakePlayers.getOrDefault(player.getGameProfile().getName(), CONSTANT_EMPTY_LIST).size();
        return current < limit;
    }

    public void addFakePlayer(@NotNull ServerPlayerEntity player, String fakePlayer) {
        this.player2fakePlayers.computeIfAbsent(player.getGameProfile().getName(), k -> new ArrayList<>()).add(fakePlayer);
    }

    public boolean canManipulateFakePlayer(@NotNull CommandContext<ServerCommandSource> ctx, String fakePlayer) {
        // IMPORTANT: disable /player ... shadow command for online-player
        if (ctx.getNodes().get(2).getNode().getName().equals("shadow")) return false;

        // bypass: console
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player == null) return true;

        // bypass: op
        if (ServerHelper.getDefaultServer().getPlayerManager().isOperator(player.getGameProfile())) return true;

        ArrayList<String> myFakePlayers = this.player2fakePlayers.getOrDefault(player.getGameProfile().getName(), CONSTANT_EMPTY_LIST);
        return myFakePlayers.contains(fakePlayer);
    }

    public int getCurrentAmountLimit() {
        ArrayList<List<Integer>> rules = Configs.configHandler.model().modules.gameplay.carpet.fake_player_manager.caps_limit_rule;
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        int currentDays = currentDate.getDayOfWeek().getValue();
        int currentMinutes = currentTime.getHour() * 60 + currentTime.getMinute();
        for (List<Integer> rule : rules) {
            if (currentDays >= rule.get(0) && currentMinutes >= rule.get(1)) return rule.get(2);
        }
        return -1;
    }

    public boolean isMyFakePlayer(@NotNull ServerPlayerEntity player, @NotNull ServerPlayerEntity fakePlayer) {
        return player2fakePlayers.getOrDefault(player.getGameProfile().getName(), CONSTANT_EMPTY_LIST).contains(fakePlayer.getGameProfile().getName());
    }

    public @NotNull GameProfile createOfflineGameProfile(@NotNull String fakePlayerName) {
        UUID offlinePlayerUUID = Uuids.getOfflinePlayerUuid(fakePlayerName);
        return new GameProfile(offlinePlayerUUID, fakePlayerName);
    }

}
