package io.github.sakurawald.core.structure;

import io.github.sakurawald.core.accessor.PlayerCombatExtension;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.manager.impl.bossbar.BossBarTicket;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class TeleportTicket extends BossBarTicket {
    private final @NotNull ServerPlayerEntity player;
    private final SpatialPose source;
    private final SpatialPose destination;

    private TeleportTicket(@NotNull ServerPlayerEntity player, SpatialPose source, SpatialPose destination, float progress) {
        super(BossBar.bossBar(LocaleHelper.getTextByKey(player, "teleport_warmup.bossbar.name"), progress, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS), Configs.configHandler.getModel().modules.teleport_warmup.warmup_second * 1000, List.of(player)
        );
        this.player = player;
        this.source = source;
        this.destination = destination;
    }

    public static @NotNull TeleportTicket of(@NotNull ServerPlayerEntity player, SpatialPose source, SpatialPose destination) {
        return new TeleportTicket(player, source, destination, 0f);
    }

    public static @NotNull TeleportTicket ofInstantTicket(@NotNull ServerPlayerEntity player, SpatialPose source, SpatialPose destination) {
        return new TeleportTicket(player, source, destination, 1f);
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
        final double INTERRUPT_DISTANCE = Configs.configHandler.getModel().modules.teleport_warmup.interrupt_distance;
        if (player.getPos().squaredDistanceTo(this.source.getX(), this.source.getY(), this.source.getZ()) >= INTERRUPT_DISTANCE) {
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
