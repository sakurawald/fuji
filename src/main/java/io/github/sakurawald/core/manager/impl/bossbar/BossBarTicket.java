package io.github.sakurawald.core.manager.impl.bossbar;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class BossBarTicket {

    private final float totalTicks;
    private final float deltaPerTick;
    private final ServerBossBar bossBar;

    @Setter
    private boolean completed;

    @Setter
    private boolean aborted;

    public BossBarTicket(ServerBossBar bossBar, int totalMS, @NotNull List<ServerPlayerEntity> players) {
        this.bossBar = bossBar;
        this.totalTicks = 20 * ((float) totalMS / 1000);
        this.deltaPerTick = 1F / this.totalTicks;

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
    protected void onPlayerDisconnected(ServerPlayerEntity player) {
        // no-op
    }

    /***
     * @return abort this ticket
     */
    protected boolean preProgressChange() {
        return true;
    }

    /***
     * @return abort this ticket
     */
    protected boolean postProgressChange() {
        return true;
    }

    protected abstract void onComplete();

}
