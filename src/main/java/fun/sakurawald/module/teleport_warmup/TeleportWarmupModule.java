package fun.sakurawald.module.teleport_warmup;

import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TeleportWarmupModule {

    public static final HashMap<ServerPlayerEntity, TeleportTicket> tickets = new HashMap<>();
    private static final float MAX_VALUE = 20 * ConfigManager.configWrapper.instance().modules.teleport_warmup.warmup_second;
    private static final float DELFA_PERCENT = 1F / MAX_VALUE;
    private static final double INTERRUPT_DISTANCE = ConfigManager.configWrapper.instance().modules.teleport_warmup.interrupt_distance;

    public static void onServerTick(MinecraftServer server) {
        Iterator<Map.Entry<ServerPlayerEntity, TeleportTicket>> iterator = tickets.entrySet().iterator();
        while (iterator.hasNext()) {

            Map.Entry<ServerPlayerEntity, TeleportTicket> pair = iterator.next();
            ServerBossBar bossbar = pair.getValue().bossbar;
            bossbar.setPercent(bossbar.getPercent() + DELFA_PERCENT);

            ServerPlayerEntity player = (ServerPlayerEntity) bossbar.getPlayers().toArray()[0];
            TeleportTicket teleportTicket = TeleportWarmupModule.tickets.get(player);

            if (((ServerPlayerEntityAccessor) player).sakurawald$inCombat()) {
                bossbar.setVisible(false);
                iterator.remove();
                MessageUtil.message(player, ConfigManager.configWrapper.instance().modules.teleport_warmup.in_combat_message, true);
                continue;
            }

            if (player.getPos().distanceTo(teleportTicket.source) >= INTERRUPT_DISTANCE) {
                bossbar.setVisible(false);
                iterator.remove();
                continue;
            }

            if (bossbar.getPercent() >= 1.0F) {
                bossbar.setVisible(false);

                teleportTicket.ready = true;
                player.teleport(teleportTicket.world, teleportTicket.destination.x, teleportTicket.destination.y, teleportTicket.destination.z, teleportTicket.yaw, teleportTicket.pitch);
                iterator.remove();
            }
        }
    }
}
