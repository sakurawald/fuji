package io.github.sakurawald.module.initializer.teleport_warmup;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class TeleportWarmupModule extends ModuleInitializer {

    public final HashMap<String, TeleportTicket> tickets = new HashMap<>();


    @Override
    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register(this::onServerTick);
    }

    @SuppressWarnings("unused")
    public void onServerTick(MinecraftServer server) {
        if (tickets.isEmpty()) return;

        Iterator<Map.Entry<String, TeleportTicket>> iterator = tickets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, TeleportTicket> pair = iterator.next();
            TeleportTicket ticket = pair.getValue();
            BossBar bossbar = ticket.bossbar;

            // fix: bossbar.progress() may be greater than 1.0F and throw an IllegalArgumentException.
            final float MAX_VALUE = 20 * Configs.configHandler.model().modules.teleport_warmup.warmup_second;
            final float DELFA_PERCENT = 1F / MAX_VALUE;
            try {
                bossbar.progress(Math.min(1f, bossbar.progress() + DELFA_PERCENT));
            } catch (Exception e) {
                // fix: if the player is disconnected, the bossbar.progress() will be throw.
                iterator.remove();
                return;
            }

            ServerPlayerEntity player = ticket.player;
            if (((ServerPlayerCombatStateAccessor) player).fuji$inCombat()) {
                bossbar.removeViewer(player);
                iterator.remove();
                MessageUtil.sendActionBar(player, "teleport_warmup.in_combat");
                continue;
            }

            final double INTERRUPT_DISTANCE = Configs.configHandler.model().modules.teleport_warmup.interrupt_distance;
            if (player.getPos().squaredDistanceTo(ticket.source.getX(), ticket.source.getY(), ticket.source.getZ()) >= INTERRUPT_DISTANCE) {
                bossbar.removeViewer(player);
                iterator.remove();
                continue;
            }

            // even the ServerPlayer is disconnected, the bossbar will still be ticked.
            if (Float.compare(bossbar.progress(), 1f) == 0) {
                bossbar.removeViewer(player);

                // don't change the order of the following two lines.
                ticket.ready = true;
                ticket.destination.teleport(player);
                iterator.remove();
            }
        }

    }

}
