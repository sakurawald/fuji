package io.github.sakurawald.module.initializer.echo.send_bossbar.structure;

import io.github.sakurawald.core.manager.impl.bossbar.BossBarTicket;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SendBossbarTicket extends BossBarTicket {

    final Runnable onComplete;

    public SendBossbarTicket(ServerBossBar bossBar, int totalMS, @NotNull List<ServerPlayerEntity> players, Runnable onComplete) {
        super(bossBar, totalMS, players);
        this.onComplete = onComplete;
    }

    @Override
    protected void onComplete() {
        this.onComplete.run();
    }
}
