package fun.sakurawald.module.teleport_warmup;

import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.AbstractModule;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static fun.sakurawald.util.MessageUtil.sendActionBar;


@Slf4j
public class TeleportWarmupModule extends AbstractModule {

    public final HashMap<ServerPlayer, TeleportTicket> tickets = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register(this::onServerTick);
    }

    @SuppressWarnings("unused")
    public void onServerTick(MinecraftServer server) {
        if (tickets.isEmpty()) return;

        Iterator<Map.Entry<ServerPlayer, TeleportTicket>> iterator = tickets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ServerPlayer, TeleportTicket> pair = iterator.next();
            TeleportTicket ticket = pair.getValue();
            BossBar bossbar = ticket.bossbar;

            // fix: bossbar.progress() may be greater than 1.0F and throw an IllegalArgumentException.
            final float MAX_VALUE = 20 * ConfigManager.configWrapper.instance().modules.teleport_warmup.warmup_second;
            final float DELFA_PERCENT = 1F / MAX_VALUE;
            try {
                bossbar.progress(Math.min(1f, bossbar.progress() + DELFA_PERCENT));
            } catch (Exception e) {
                // fix: if the player is disconnected, the bossbar.progress() will be throw.
                iterator.remove();
                return;
            }

            ServerPlayer player = ticket.player;
            if (((ServerPlayerAccessor) player).sakurawald$inCombat()) {
                bossbar.removeViewer(player);
                iterator.remove();
                sendActionBar(player, "teleport_warmup.in_combat");
                continue;
            }

            final double INTERRUPT_DISTANCE = ConfigManager.configWrapper.instance().modules.teleport_warmup.interrupt_distance;
            if (player.position().distanceToSqr(ticket.source.getX(), ticket.source.getY(), ticket.source.getZ()) >= INTERRUPT_DISTANCE) {
                bossbar.removeViewer(player);
                iterator.remove();
                continue;
            }

            // even the ServerPlayer is disconnected, the bossbar will still be ticked.
            if (Float.compare(bossbar.progress(), 1f) == 0) {
                bossbar.removeViewer(player);

                // don't change the order of the following two lines.
                ticket.ready = true;
                player.teleportTo((ServerLevel) ticket.destination.getLevel(), ticket.destination.getX(), ticket.destination.getY(), ticket.destination.getZ(), ticket.destination.getYaw(), ticket.destination.getPitch());
                iterator.remove();
            }
        }

    }

}
