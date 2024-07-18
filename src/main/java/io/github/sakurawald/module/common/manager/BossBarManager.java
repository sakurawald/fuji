package io.github.sakurawald.module.common.manager;

import com.google.common.collect.ImmutableList;
import io.github.sakurawald.module.common.structure.BossBarTicket;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kyori.adventure.audience.Audience;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("LombokGetterMayBeUsed")
@Slf4j
public class BossBarManager {

    @Getter
    private final List<BossBarTicket> tickets = new ArrayList<>();

    public void addTicket(BossBarTicket ticket) {
        this.tickets.add(ticket);
    }

    public void initialize() {
        ServerTickEvents.START_SERVER_TICK.register(this::onServerTick);
    }

    private void abortTicket(Iterator<BossBarTicket> iterator, BossBarTicket ticket){
        ticket.clearAudiences();
        iterator.remove();
    }

    private void onServerTick(MinecraftServer server) {
        if (tickets.isEmpty()) return;

        Iterator<BossBarTicket> ticketIter = tickets.iterator();
        while (ticketIter.hasNext()) {
            BossBarTicket ticket = ticketIter.next();

            if (ticket.isAborted()) {
                abortTicket(ticketIter, ticket);
                continue;
            }

            if (!ticket.preProgressChange()) {
                abortTicket(ticketIter, ticket);
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
                abortTicket(ticketIter, ticket);
                return;
            }

            if (!ticket.postProgressChange()) {
                abortTicket(ticketIter, ticket);
                continue;
            }

            // even the ServerPlayer is disconnected, the bossbar will still be ticked.
            if (Float.compare(ticket.progress(), 1f) == 0) {
                ticket.onComplete();
                abortTicket(ticketIter, ticket);
            }
        }
    }

}
