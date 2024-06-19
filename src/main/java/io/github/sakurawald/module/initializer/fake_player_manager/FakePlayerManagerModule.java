package io.github.sakurawald.module.initializer.fake_player_manager;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.common.event.PostPlayerConnectEvent;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.text.Component;
import net.minecraft.client.realms.Request;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Uuids;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class FakePlayerManagerModule extends ModuleInitializer {
    private final ArrayList<String> CONSTANT_EMPTY_LIST = new ArrayList<>();
    private final HashMap<String, ArrayList<String>> player2fakePlayers = new HashMap<>();
    private final HashMap<String, Long> player2expiration = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::registerScheduleTask);

        PostPlayerConnectEvent.EVENT.register((connection, player, commonListenerCookie) -> {
            if (CarpetUtil.isFakePlayer(player)) return ActionResult.PASS;
            if (this.hasFakePlayers(player)) {
                this.renewFakePlayers(player);
            }
            return ActionResult.PASS;
        });
    }

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("player").then(
                        CommandManager.literal("who").executes(this::$who)
                ).then(
                        CommandManager.literal("renew").executes(this::$renew)
                )
        );
    }

    @SuppressWarnings("SameReturnValue")
    private int $renew(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            renewFakePlayers(player);
            return Command.SINGLE_SUCCESS;
        });
    }


    private int $who(CommandContext<ServerCommandSource> context) {
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
        source.sendMessage(MessageUtil.ofComponent(source, "fake_player_manager.who.header").append(Component.text(builder.toString())));
        return Command.SINGLE_SUCCESS;
    }

    public boolean hasFakePlayers(ServerPlayerEntity player) {
        validateFakePlayers();
        return player2fakePlayers.containsKey(player.getGameProfile().getName());
    }

    public void renewFakePlayers(ServerPlayerEntity player) {
        String name = player.getGameProfile().getName();
        int duration = Configs.configHandler.model().modules.fake_player_manager.renew_duration_ms;
        long newTime = System.currentTimeMillis() + duration;
        player2expiration.put(name, newTime);
        MessageUtil.sendMessage(player, "fake_player_manager.renew.success", DateUtil.toStandardDateFormat(newTime));
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
                ServerPlayerEntity fakePlayer = Fuji.SERVER.getPlayerManager().getPlayer(name);
                return fakePlayer == null || fakePlayer.isRemoved();
            });
        }
    }

    public boolean canSpawnFakePlayer(ServerPlayerEntity player) {
        /* validate */
        validateFakePlayers();

        /* check */
        int limit = this.getCurrentAmountLimit();
        int current = this.player2fakePlayers.getOrDefault(player.getGameProfile().getName(), CONSTANT_EMPTY_LIST).size();
        return current < limit;
    }

    public void addFakePlayer(ServerPlayerEntity player, String fakePlayer) {
        this.player2fakePlayers.computeIfAbsent(player.getGameProfile().getName(), k -> new ArrayList<>()).add(fakePlayer);
    }

    public boolean canManipulateFakePlayer(CommandContext<ServerCommandSource> ctx, String fakePlayer) {
        // IMPORTANT: disable /player ... shadow command for online-player
        if (ctx.getNodes().get(2).getNode().getName().equals("shadow")) return false;

        // bypass: console
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player == null) return true;

        // bypass: op
        if (Fuji.SERVER.getPlayerManager().isOperator(player.getGameProfile())) return true;

        ArrayList<String> myFakePlayers = this.player2fakePlayers.getOrDefault(player.getGameProfile().getName(), CONSTANT_EMPTY_LIST);
        return myFakePlayers.contains(fakePlayer);
    }

    private int getCurrentAmountLimit() {
        ArrayList<List<Integer>> rules = Configs.configHandler.model().modules.fake_player_manager.caps_limit_rule;
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
        ScheduleUtil.addJob(ManageFakePlayersJob.class, null, null, ScheduleUtil.CRON_EVERY_MINUTE, new JobDataMap() {
            {
                this.put(FakePlayerManagerModule.class.getName(), FakePlayerManagerModule.this);
            }
        });
    }

    public boolean isMyFakePlayer(ServerPlayerEntity player, ServerPlayerEntity fakePlayer) {
        return player2fakePlayers.getOrDefault(player.getGameProfile().getName(), CONSTANT_EMPTY_LIST).contains(fakePlayer.getGameProfile().getName());
    }

    public GameProfile createOfflineGameProfile(String fakePlayerName) {
        UUID offlinePlayerUUID = Uuids.getOfflinePlayerUuid(fakePlayerName);
        return new GameProfile(offlinePlayerUUID, fakePlayerName);
    }

    public static class ManageFakePlayersJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            /* validate */
            FakePlayerManagerModule module = (FakePlayerManagerModule) context.getJobDetail().getJobDataMap().get(FakePlayerManagerModule.class.getName());
            module.validateFakePlayers();

            int limit = module.getCurrentAmountLimit();
            long currentTimeMS = System.currentTimeMillis();
            for (String playerName : module.player2fakePlayers.keySet()) {
                /* check for renew limits */
                long expiration = module.player2expiration.getOrDefault(playerName, 0L);
                ArrayList<String> fakePlayers = module.player2fakePlayers.getOrDefault(playerName, module.CONSTANT_EMPTY_LIST);
                if (expiration <= currentTimeMS) {
                    /* auto-renew for online-playerName */
                    ServerPlayerEntity playerByName = Fuji.SERVER.getPlayerManager().getPlayer(playerName);
                    if (playerByName != null) {
                        module.renewFakePlayers(playerByName);
                        continue;
                    }

                    for (String fakePlayerName : fakePlayers) {
                        ServerPlayerEntity fakePlayer = Fuji.SERVER.getPlayerManager().getPlayer(fakePlayerName);
                        if (fakePlayer == null) return;
                        fakePlayer.kill();
                        MessageUtil.sendBroadcast("fake_player_manager.kick_for_expiration", fakePlayer.getGameProfile().getName(), playerName);
                    }
                    // remove entry
                    module.player2expiration.remove(playerName);

                    // we'll kick all fake players, so we don't need to check for amount limits
                    continue;
                }

                /* check for amount limits */
                for (int i = fakePlayers.size() - 1; i >= limit; i--) {
                    ServerPlayerEntity fakePlayer = Fuji.SERVER.getPlayerManager().getPlayer(fakePlayers.get(i));
                    if (fakePlayer == null) continue;
                    fakePlayer.kill();

                    MessageUtil.sendBroadcast("fake_player_manager.kick_for_amount", fakePlayer.getGameProfile().getName(), playerName);
                }
            }
        }
    }
}
