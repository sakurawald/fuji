package io.github.sakurawald.core.manager.impl.bossbar;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class BossBarTicket {
    @Getter
    private final float maxValue;
    @Getter
    private final float deltaValue;
    private final ServerBossBar bossBar;

    @Getter
    @Setter
    private boolean completed;

    @Getter
    @Setter
    private boolean aborted;

    public BossBarTicket(ServerBossBar bossBar, int totalMS, @NotNull List<ServerPlayerEntity> players) {
        this.bossBar = bossBar;
        this.maxValue = 20 * ((float) totalMS / 1000);
        this.deltaValue = 1F / this.maxValue;

        // the default percent is 1.0f
        this.bossBar.setPercent(0);

        // add players for this bossbar
        players.forEach(this::addPlayer);

        this.aborted = false;
    }

    public @NotNull Collection<ServerPlayerEntity> getPlayers() {
        return Collections.unmodifiableCollection(this.bossBar.getPlayers());
    }

    public float progress() {
        return this.bossBar.getPercent();
    }

    public void progress(float progress) {
        this.bossBar.setPercent(progress);
    }

    public void addPlayer(@NotNull ServerPlayerEntity player) {
        this.bossBar.addPlayer(player);
    }

    public void removePlayer(@NotNull ServerPlayerEntity player) {
        this.bossBar.removePlayer(player);
    }

    public void clearPlayers() {
        this.bossBar.setVisible(false);
        this.bossBar.clearPlayers();
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    public void onPlayerDisconnected(ServerPlayerEntity player) {
        // no-op
    }

    /***
     * @return ticket valid
     */
    public boolean preProgressChange() {
        return true;
    }

    /***
     * @return ticket valid
     */
    public boolean postProgressChange() {
        return true;
    }

    public abstract void onComplete();

}
