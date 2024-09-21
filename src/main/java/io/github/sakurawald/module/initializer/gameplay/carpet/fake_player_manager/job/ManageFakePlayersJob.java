package io.github.sakurawald.module.initializer.gameplay.carpet.fake_player_manager.job;

import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.job.abst.CronJob;
import io.github.sakurawald.core.manager.impl.scheduler.ScheduleManager;
import io.github.sakurawald.module.initializer.gameplay.carpet.fake_player_manager.FakePlayerManagerInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;

import java.util.List;

public class ManageFakePlayersJob extends CronJob {


    public ManageFakePlayersJob() {
        super(() -> ScheduleManager.CRON_EVERY_MINUTE);
    }

    @Override
    public void execute(@NotNull JobExecutionContext context) {
        /* validate */
        FakePlayerManagerInitializer.validateFakePlayers();

        int limit = FakePlayerManagerInitializer.getCurrentAmountLimit();
        long currentTimeMS = System.currentTimeMillis();
        for (String playerName : FakePlayerManagerInitializer.player2fakePlayers.keySet()) {
            /* check for renew limits */
            long expiration = FakePlayerManagerInitializer.player2expiration.getOrDefault(playerName, 0L);
            List<String> fakePlayers = FakePlayerManagerInitializer.player2fakePlayers.getOrDefault(playerName, FakePlayerManagerInitializer.CONSTANT_EMPTY_LIST);
            if (expiration <= currentTimeMS) {
                /* auto-renew for online-playerName */
                ServerPlayerEntity playerByName = ServerHelper.getDefaultServer().getPlayerManager().getPlayer(playerName);
                if (playerByName != null) {
                    FakePlayerManagerInitializer.renewFakePlayers(playerByName);
                    continue;
                }

                for (String fakePlayerName : fakePlayers) {
                    ServerPlayerEntity fakePlayer = ServerHelper.getDefaultServer().getPlayerManager().getPlayer(fakePlayerName);
                    if (fakePlayer == null) return;
                    fakePlayer.kill();
                    LocaleHelper.sendBroadcastByKey("fake_player_manager.kick_for_expiration", fakePlayer.getGameProfile().getName(), playerName);
                }
                // remove entry
                FakePlayerManagerInitializer.player2expiration.remove(playerName);

                // we'll kick all fake players, so we don't need to check for amount limits
                continue;
            }

            /* check for amount limits */
            for (int i = fakePlayers.size() - 1; i >= limit; i--) {
                ServerPlayerEntity fakePlayer = ServerHelper.getDefaultServer().getPlayerManager().getPlayer(fakePlayers.get(i));
                if (fakePlayer == null) continue;
                fakePlayer.kill();

                LocaleHelper.sendBroadcastByKey("fake_player_manager.kick_for_amount", fakePlayer.getGameProfile().getName(), playerName);
            }
        }
    }
}
