package io.github.sakurawald.module.initializer.afk.job;

import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.job.impl.NPassMarkerJob;
import io.github.sakurawald.module.initializer.afk.accessor.AfkStateAccessor;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class AfkMarkerJob extends NPassMarkerJob<ServerPlayerEntity> {

    public AfkMarkerJob() {
        super(1, () -> Configs.configHandler.model().modules.afk.afk_checker.cron);
    }

    @Override
    public Collection<ServerPlayerEntity> getEntityList() {
        return ServerHelper.getDefaultServer().getPlayerManager().getPlayerList();
    }

    @Override
    public boolean shouldMark(ServerPlayerEntity entity) {
        if (entity.isRemoved()) return false;

        // get last action time
        AfkStateAccessor afk_player = (AfkStateAccessor) entity;
        long lastActionTime = entity.getLastActionTime();
        long snapshotLastActionTime = afk_player.fuji$getSnapshotLastActionTime();

        // update snapshotLastActionTime
        afk_player.fuji$setSnapshotLastActionTime(lastActionTime);

        // diff last action time
        return (lastActionTime - snapshotLastActionTime) < 3000;
    }

    @Override
    public void onCompleted(ServerPlayerEntity entity) {
        AfkStateAccessor afk_player = (AfkStateAccessor) entity;
        // ignore if already in afk
        if (!afk_player.fuji$isAfk()) {
            afk_player.fuji$setAfk(true);
        }
    }
}
