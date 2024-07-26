package io.github.sakurawald.module.common.structure;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.accessor.ServerPlayerCombatStateAccessor;
import io.github.sakurawald.util.minecraft.MessageHelper;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class TeleportTicket extends BossBarTicket {
    private final @NotNull ServerPlayerEntity player;
    private final Position source;
    private final Position destination;

    private TeleportTicket(@NotNull ServerPlayerEntity player, Position source, Position destination, float progress) {
        super(BossBar.bossBar(MessageHelper.ofComponent(player, "teleport_warmup.bossbar.name"), progress, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS), Configs.configHandler.model().modules.teleport_warmup.warmup_second * 1000, List.of(player)
        );
        this.player = player;
        this.source = source;
        this.destination = destination;
    }

    public static @NotNull TeleportTicket of(@NotNull ServerPlayerEntity player, Position source, Position destination) {
        return new TeleportTicket(player, source, destination, 0f);
    }

    public static @NotNull TeleportTicket ofInstantTicket(@NotNull ServerPlayerEntity player, Position source, Position destination) {
        return new TeleportTicket(player, source, destination, 1f);
    }

    @Override
    public boolean preProgressChange() {

        // check combat
        if (((ServerPlayerCombatStateAccessor) player).fuji$inCombat()) {
            MessageHelper.sendActionBar(player, "teleport_warmup.in_combat");
            return false;
        }

        // check damage
        final double INTERRUPT_DISTANCE = Configs.configHandler.model().modules.teleport_warmup.interrupt_distance;
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
