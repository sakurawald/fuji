package io.github.sakurawald.core.structure;

import io.github.sakurawald.core.accessor.PlayerCombatExtension;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.manager.impl.bossbar.BossBarTicket;
import lombok.Getter;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class TeleportTicket extends BossBarTicket {
    private final @NotNull ServerPlayerEntity player;
    private final SpatialPose source;
    private final SpatialPose destination;

    private final double interruptDistance;

    private TeleportTicket(@NotNull ServerPlayerEntity player, SpatialPose source, SpatialPose destination, float progress, int totalMs, double interruptDistance) {
        super(
            new ServerBossBar(LocaleHelper.getTextByKey(player, "teleport_warmup.bossbar.name"), BossBar.Color.BLUE, net.minecraft.entity.boss.BossBar.Style.PROGRESS)
            , totalMs
            , List.of(player)
        );
        this.player = player;
        this.source = source;
        this.destination = destination;
        this.interruptDistance = interruptDistance;

        // set progress
        this.getBossBar().setPercent(progress);
    }

    public static @NotNull TeleportTicket of(@NotNull ServerPlayerEntity player, SpatialPose source, SpatialPose destination, int totalMs, double interruptDistance) {
        return new TeleportTicket(player, source, destination, 0f, totalMs, interruptDistance);
    }

    public static @NotNull TeleportTicket ofInstantTicket(@NotNull ServerPlayerEntity player, SpatialPose source, SpatialPose destination, int totalMs, double interruptDistance) {
        return new TeleportTicket(player, source, destination, 1f, totalMs, interruptDistance);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean preProgressChange() {

        // check combat
        if (((PlayerCombatExtension) player).fuji$inCombat()) {
            LocaleHelper.sendActionBarByKey(player, "teleport_warmup.in_combat");
            return false;
        }

        // check damage
        if (player.getPos().squaredDistanceTo(this.source.getX(), this.source.getY(), this.source.getZ()) >= this.interruptDistance) {
            return false;
        }

        return true;
    }

    @Override
    public void onComplete() {
        // set ready before teleport
        if (!player.isDisconnected()) {
            destination.teleport(player);
        }
    }

}
