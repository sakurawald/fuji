package io.github.sakurawald.module.initializer.better_fake_player;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.TimeUtil;
import io.github.sakurawald.util.MessageUtil;
import io.github.sakurawald.util.ScheduleUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class BetterFakePlayerModule extends ModuleInitializer {
    private final ArrayList<String> CONSTANT_EMPTY_LIST = new ArrayList<>();
    private final HashMap<String, ArrayList<String>> player2fakePlayers = new HashMap<>();
    private final HashMap<String, Long> player2expiration = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::registerScheduleTask);
    }

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("player").then(
                        Commands.literal("who").executes(this::$who)
                ).then(
                        Commands.literal("renew").executes(this::$renew)
                )
        );
    }

    @SuppressWarnings("SameReturnValue")
    private int $renew(CommandContext<CommandSourceStack> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            renewFakePlayers(player);
            return Command.SINGLE_SUCCESS;
        });
    }


    private int $who(CommandContext<CommandSourceStack> context) {
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
        CommandSourceStack source = context.getSource();
        source.sendMessage(MessageUtil.ofComponent(source, "better_fake_player.who.header").append(Component.text(builder.toString())));
        return Command.SINGLE_SUCCESS;
    }

    public boolean hasFakePlayers(ServerPlayer player) {
        validateFakePlayers();
        return player2fakePlayers.containsKey(player.getGameProfile().getName());
    }

    public void renewFakePlayers(ServerPlayer player) {
        String name = player.getGameProfile().getName();
        int duration = Configs.configHandler.model().modules.better_fake_player.renew_duration_ms;
        long newTime = System.currentTimeMillis() + duration;
        player2expiration.put(name, newTime);
        MessageUtil.sendMessage(player, "better_fake_player.renew.success", TimeUtil.toStandardDateFormat(newTime));
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
                ServerPlayer fakePlayer = Fuji.SERVER.getPlayerList().getPlayerByName(name);
                return fakePlayer == null || fakePlayer.isRemoved();
            });
        }
    }

    public boolean canSpawnFakePlayer(ServerPlayer player) {
        /* validate */
        validateFakePlayers();

        /* check */
        int limit = this.getCurrentAmountLimit();
        int current = this.player2fakePlayers.getOrDefault(player.getGameProfile().getName(), CONSTANT_EMPTY_LIST).size();
        return current < limit;
    }

    public void addFakePlayer(ServerPlayer player, String fakePlayer) {
        this.player2fakePlayers.computeIfAbsent(player.getGameProfile().getName(), k -> new ArrayList<>()).add(fakePlayer);
    }

    public boolean canManipulateFakePlayer(CommandContext<CommandSourceStack> ctx, String fakePlayer) {
        // IMPORTANT: disable /player ... shadow command for online-player
        if (ctx.getNodes().get(2).getNode().getName().equals("shadow")) return false;

        // bypass: console
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return true;

        // bypass: op
        if (Fuji.SERVER.getPlayerList().isOp(player.getGameProfile())) return true;

        ArrayList<String> myFakePlayers = this.player2fakePlayers.getOrDefault(player.getGameProfile().getName(), CONSTANT_EMPTY_LIST);
        return myFakePlayers.contains(fakePlayer);
    }

    private int getCurrentAmountLimit() {
        ArrayList<List<Integer>> rules = Configs.configHandler.model().modules.better_fake_player.caps_limit_rule;
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
                this.put(BetterFakePlayerModule.class.getName(), BetterFakePlayerModule.this);
            }
        });
    }

    public boolean isMyFakePlayer(ServerPlayer player, ServerPlayer fakePlayer) {
        return player2fakePlayers.getOrDefault(player.getGameProfile().getName(), CONSTANT_EMPTY_LIST).contains(fakePlayer.getGameProfile().getName());
    }

    public GameProfile createOfflineGameProfile(String fakePlayerName) {
        UUID offlinePlayerUUID = UUIDUtil.createOfflinePlayerUUID(fakePlayerName);
        return new GameProfile(offlinePlayerUUID, fakePlayerName);
    }

    public static class ManageFakePlayersJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            /* validate */
            BetterFakePlayerModule module = (BetterFakePlayerModule) context.getJobDetail().getJobDataMap().get(BetterFakePlayerModule.class.getName());
            module.validateFakePlayers();

            int limit = module.getCurrentAmountLimit();
            long currentTimeMS = System.currentTimeMillis();
            for (String playerName : module.player2fakePlayers.keySet()) {
                /* check for renew limits */
                long expiration = module.player2expiration.getOrDefault(playerName, 0L);
                ArrayList<String> fakePlayers = module.player2fakePlayers.getOrDefault(playerName, module.CONSTANT_EMPTY_LIST);
                if (expiration <= currentTimeMS) {
                    /* auto-renew for online-playerName */
                    ServerPlayer playerByName = Fuji.SERVER.getPlayerList().getPlayerByName(playerName);
                    if (playerByName != null) {
                        module.renewFakePlayers(playerByName);
                        continue;
                    }

                    for (String fakePlayerName : fakePlayers) {
                        ServerPlayer fakePlayer = Fuji.SERVER.getPlayerList().getPlayerByName(fakePlayerName);
                        if (fakePlayer == null) return;
                        fakePlayer.kill();
                        MessageUtil.sendBroadcast("better_fake_player.kick_for_expiration", fakePlayer.getGameProfile().getName(), playerName);
                    }
                    // remove entry
                    module.player2expiration.remove(playerName);

                    // we'll kick all fake players, so we don't need to check for amount limits
                    continue;
                }

                /* check for amount limits */
                for (int i = fakePlayers.size() - 1; i >= limit; i--) {
                    ServerPlayer fakePlayer = Fuji.SERVER.getPlayerList().getPlayerByName(fakePlayers.get(i));
                    if (fakePlayer == null) continue;
                    fakePlayer.kill();

                    MessageUtil.sendBroadcast("better_fake_player.kick_for_amount", fakePlayer.getGameProfile().getName(), playerName);
                }
            }
        }
    }
}
