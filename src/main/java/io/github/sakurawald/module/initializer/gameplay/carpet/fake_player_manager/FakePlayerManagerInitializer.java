package io.github.sakurawald.module.initializer.gameplay.carpet.fake_player_manager;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.common.manager.scheduler.ScheduleManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.DateUtil;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.NotNull;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class FakePlayerManagerInitializer extends ModuleInitializer {
    private final ArrayList<String> CONSTANT_EMPTY_LIST = new ArrayList<>();
    private final HashMap<String, ArrayList<String>> player2fakePlayers = new HashMap<>();
    private final HashMap<String, Long> player2expiration = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::registerScheduleTask);
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

    private void validateFakePlayers() {
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

    private int getCurrentAmountLimit() {
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

    @SuppressWarnings("unused")
    public void registerScheduleTask(MinecraftServer server) {
        Managers.getScheduleManager().scheduleJob(ManageFakePlayersJob.class, ScheduleManager.CRON_EVERY_MINUTE, new JobDataMap() {
            {
                this.put(FakePlayerManagerInitializer.class.getName(), FakePlayerManagerInitializer.this);
            }
        });
    }

    public boolean isMyFakePlayer(@NotNull ServerPlayerEntity player, @NotNull ServerPlayerEntity fakePlayer) {
        return player2fakePlayers.getOrDefault(player.getGameProfile().getName(), CONSTANT_EMPTY_LIST).contains(fakePlayer.getGameProfile().getName());
    }

    public @NotNull GameProfile createOfflineGameProfile(@NotNull String fakePlayerName) {
        UUID offlinePlayerUUID = Uuids.getOfflinePlayerUuid(fakePlayerName);
        return new GameProfile(offlinePlayerUUID, fakePlayerName);
    }

    public static class ManageFakePlayersJob implements Job {

        @Override
        public void execute(@NotNull JobExecutionContext context) {
            /* validate */
            FakePlayerManagerInitializer module = (FakePlayerManagerInitializer) context.getJobDetail().getJobDataMap().get(FakePlayerManagerInitializer.class.getName());
            module.validateFakePlayers();

            int limit = module.getCurrentAmountLimit();
            long currentTimeMS = System.currentTimeMillis();
            for (String playerName : module.player2fakePlayers.keySet()) {
                /* check for renew limits */
                long expiration = module.player2expiration.getOrDefault(playerName, 0L);
                ArrayList<String> fakePlayers = module.player2fakePlayers.getOrDefault(playerName, module.CONSTANT_EMPTY_LIST);
                if (expiration <= currentTimeMS) {
                    /* auto-renew for online-playerName */
                    ServerPlayerEntity playerByName = ServerHelper.getDefaultServer().getPlayerManager().getPlayer(playerName);
                    if (playerByName != null) {
                        module.renewFakePlayers(playerByName);
                        continue;
                    }

                    for (String fakePlayerName : fakePlayers) {
                        ServerPlayerEntity fakePlayer = ServerHelper.getDefaultServer().getPlayerManager().getPlayer(fakePlayerName);
                        if (fakePlayer == null) return;
                        fakePlayer.kill();
                        MessageHelper.sendBroadcast("fake_player_manager.kick_for_expiration", fakePlayer.getGameProfile().getName(), playerName);
                    }
                    // remove entry
                    module.player2expiration.remove(playerName);

                    // we'll kick all fake players, so we don't need to check for amount limits
                    continue;
                }

                /* check for amount limits */
                for (int i = fakePlayers.size() - 1; i >= limit; i--) {
                    ServerPlayerEntity fakePlayer = ServerHelper.getDefaultServer().getPlayerManager().getPlayer(fakePlayers.get(i));
                    if (fakePlayer == null) continue;
                    fakePlayer.kill();

                    MessageHelper.sendBroadcast("fake_player_manager.kick_for_amount", fakePlayer.getGameProfile().getName(), playerName);
                }
            }
        }
    }
}
