package fun.sakurawald.module.teleport_warmup;

import fun.sakurawald.config.ConfigManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.phys.Vec3;

public class TeleportTicket {

    public ServerPlayer player;
    public ServerLevel world;
    public Vec3 source;
    public Vec3 destination;
    public float yaw;
    public float pitch;
    public boolean ready;
    public ServerBossEvent bossbar;

    public TeleportTicket(ServerPlayer player, ServerLevel world, Vec3 source, Vec3 destination, float yaw, float pitch, boolean ready) {
        this.player = player;
        this.world = world;
        this.source = source;
        this.destination = destination;
        this.yaw = yaw;
        this.pitch = pitch;
        this.ready = ready;
        this.bossbar = new ServerBossEvent(Component.nullToEmpty(ConfigManager.configWrapper.instance().modules.teleport_warmup.bossbar_name), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
        bossbar.setProgress(0f);
        bossbar.addPlayer(player);
        bossbar.setVisible(true);
    }

}
