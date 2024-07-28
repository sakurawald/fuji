package io.github.sakurawald.module.initializer.afk.job;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.job.NPassMarkerJob;
import io.github.sakurawald.module.initializer.afk.AfkStateAccessor;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class AfkMarkerJob extends NPassMarkerJob<ServerPlayerEntity> {

    public AfkMarkerJob() {
        super(1, Configs.configHandler.model().modules.afk.afk_checker.cron);
    }

    @Override
    public Collection<ServerPlayerEntity> getEntityList() {
        return ServerHelper.getDefaultServer().getPlayerManager().getPlayerList();
    }

    @Override
    public boolean shouldMark(ServerPlayerEntity entity) {
        if (entity.isRemoved()) return false;

        AfkStateAccessor afk_player = (AfkStateAccessor) entity;

        // get last action time
        long lastActionTime = entity.getLastActionTime();
        long lastLastActionTime = afk_player.fuji$getLastLastActionTime();
        afk_player.fuji$setLastLastActionTime(lastActionTime);

        // diff last action time
            /* note:
            when a player joins the server,
            we'll set lastLastActionTime's initial value to Player#getLastActionTime(),
            but there are a little difference even if you call Player#getLastActionTime() again
             */
        if (lastActionTime - lastLastActionTime <= 3000) {
            if (afk_player.fuji$isAfk()) return false;
        }

        return true;
    }

    @Override
    public void onCompleted(ServerPlayerEntity entity) {
        AfkStateAccessor afk_player = (AfkStateAccessor) entity;
        afk_player.fuji$setAfk(true);
        if (Configs.configHandler.model().modules.afk.afk_checker.kick_player) {
            entity.networkHandler.disconnect(MessageHelper.ofText(entity, "afk.kick"));
        }
    }
}
