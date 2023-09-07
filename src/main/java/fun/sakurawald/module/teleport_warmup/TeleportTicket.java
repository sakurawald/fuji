package fun.sakurawald.module.teleport_warmup;

import fun.sakurawald.config.ConfigManager;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class TeleportTicket {

    public ServerPlayerEntity player;
    public ServerWorld world;
    public Vec3d source;
    public Vec3d destination;
    public float yaw;
    public float pitch;
    public boolean ready;
    public ServerBossBar bossbar;

    public TeleportTicket(ServerPlayerEntity player, ServerWorld world, Vec3d source, Vec3d destination, float yaw, float pitch, boolean ready) {
        this.player = player;
        this.world = world;
        this.source = source;
        this.destination = destination;
        this.yaw = yaw;
        this.pitch = pitch;
        this.ready = ready;
        this.bossbar = new ServerBossBar(Text.of(ConfigManager.configWrapper.instance().modules.teleport_warmup.bossbar_name), BossBar.Color.BLUE, BossBar.Style.PROGRESS);
        bossbar.setPercent(0f);
        bossbar.addPlayer(player);
        bossbar.setVisible(true);
    }

}
