package fun.sakurawald.module.teleport_warmup;

import fun.sakurawald.util.MessageUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.server.level.ServerPlayer;

public class TeleportTicket {

    public ServerPlayer player;
    public Position source;
    public Position destination;
    public boolean ready;
    public BossBar bossbar;

    public TeleportTicket(ServerPlayer player, Position source, Position destination, boolean ready) {
        this.player = player;
        this.source = source;
        this.destination = destination;
        this.ready = ready;
        this.bossbar = BossBar.bossBar(MessageUtil.resolve(player, "teleport_warmup.bossbar.name"), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        bossbar.addViewer(player);
    }

}
