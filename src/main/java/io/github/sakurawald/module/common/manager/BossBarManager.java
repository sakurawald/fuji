package io.github.sakurawald.module.common.manager;

import com.google.common.collect.ImmutableList;
import io.github.sakurawald.module.common.structure.BossBarTicket;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kyori.adventure.audience.Audience;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import oshi.annotation.concurrent.Immutable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("LombokGetterMayBeUsed")
@Slf4j
public class BossBarManager {

    private final List<BossBarTicket> tickets = new ArrayList<>();
    private final List<BossBarTicket> addedTickets = new ArrayList<>();

    public void initialize() {
        ServerTickEvents.START_SERVER_TICK.register(this::onServerTick);
    }

    public @Immutable List<BossBarTicket> getTickets() {
        return ImmutableList.copyOf(this.tickets);
    }

    public void addTicket(BossBarTicket ticket) {
        this.addedTickets.add(ticket);
    }

    private void abortTicket(BossBarTicket ticket) {
        ticket.clearAudiences();
        this.tickets.remove(ticket);
    }

    private void onServerTick(MinecraftServer server) {
        /* add tickets */
        this.tickets.addAll(addedTickets);
        addedTickets.clear();

        /* iterate tickets */
        if (tickets.isEmpty()) return;

        List<BossBarTicket> abortedTickets = new ArrayList<>();
        List<BossBarTicket> completedTickets = new ArrayList<>();

        for (BossBarTicket ticket : tickets) {
            // is aborted ?
            if (ticket.isAborted()) {
                abortedTickets.add(ticket);
                continue;
            }

            if (!ticket.preProgressChange()) {
                ticket.setAborted(true);
                continue;
            }

            for (Audience audience : ticket.getAudiences()) {
                if (audience instanceof ServerPlayerEntity player) {
                    if (player.isDisconnected()) {
                        ticket.onAudienceDisconnected(audience);
                        ticket.removeAudience(audience);
                    }
                }
            }

            // fix: bossbar.progress() may be greater than 1.0F and throw an IllegalArgumentException.
            try {
                ticket.progress(Math.min(1f, ticket.progress() + ticket.getDeltaValue()));
            } catch (Exception e) {
                /*
                 The exception will be thrown, if
                 1. One of the viewer of the bossbar is disconnected (but not removed from the bossbar).
                 2. The viewers of the bossbar is empty.
                 */
                ticket.setAborted(true);
                return;
            }

            if (!ticket.postProgressChange()) {
                ticket.setAborted(true);
                continue;
            }

            // even the ServerPlayer is disconnected, the bossbar will still be ticked.
            if (Float.compare(ticket.progress(), 1f) == 0) {
                ticket.setReady(true);
                ticket.setAborted(true);
                completedTickets.add(ticket);
            }
        }

        /* remove tickets */
        completedTickets.forEach(BossBarTicket::onComplete);
        abortedTickets.forEach(this::abortTicket);
    }

}
