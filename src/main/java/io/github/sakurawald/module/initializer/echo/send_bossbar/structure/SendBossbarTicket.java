package io.github.sakurawald.module.initializer.echo.send_bossbar.structure;

import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.manager.impl.bossbar.BossBarTicket;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SendBossbarTicket extends BossBarTicket {

    final ServerPlayerEntity player;
    final String title;
    final Runnable onComplete;
    private float elapsedTicks;

    public SendBossbarTicket(String title, BossBar.Color color, BossBar.Style style, int totalMS, @NotNull ServerPlayerEntity player, Runnable onComplete) {
        super(new ServerBossBar(Text.empty(), color, style), totalMS, List.of(player));
        this.player = player;
        this.title = title;
        this.onComplete = onComplete;
    }

    @Override
    protected boolean preProgressChange() {
        String timeStr = this.title.replace("[total_time]", (int) this.getTotalTicks() / 20 + "s")
            .replace("[elapsed_time]", (int) this.elapsedTicks / 20 + "s");

        Text title = LocaleHelper.getTextByValue(this.player, timeStr);
        this.getBossBar().setName(title);

        this.elapsedTicks++;
        return true;
    }

    @Override
    protected void onComplete() {
        this.onComplete.run();
    }
}
