package io.github.sakurawald.module.common.structure;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.NotNull;
import oshi.annotation.concurrent.Immutable;

import java.util.ArrayList;
import java.util.List;

public abstract class BossBarTicket {
    private final @NotNull List<Audience> audiences;
    @Getter
    private final float maxValue;
    @Getter
    private final float deltaValue;
    private final BossBar bossbar;

    @Getter
    @Setter
    private boolean completed;

    @Getter
    @Setter
    private boolean aborted;

    public BossBarTicket(BossBar bossbar, int totalMS, @NotNull List<Audience> audiences) {
        this.bossbar = bossbar;
        this.maxValue = 20 * ((float) totalMS / 1000);
        this.deltaValue = 1F / this.maxValue;
        this.audiences = new ArrayList<>();
        for (Audience audience : audiences) {
            this.addAudience(audience);
        }

        this.aborted = false;
    }

    public @Immutable @NotNull List<Audience> getAudiences() {
        return ImmutableList.copyOf(this.audiences);
    }

    public float progress() {
        return this.bossbar.progress();
    }

    public @NotNull BossBar progress(float progress) {
        return this.bossbar.progress(progress);
    }

    public void addAudience(@NotNull Audience audience) {
        this.bossbar.addViewer(audience);
        this.audiences.add(audience);
    }

    public void removeAudience(@NotNull Audience audience) {
        this.bossbar.removeViewer(audience);
        this.audiences.remove(audience);
    }

    public void clearAudiences() {
        this.audiences.forEach(a -> a.hideBossBar(this.bossbar));
        this.audiences.clear();
    }

    public void onAudienceDisconnected(Audience audience) {
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
