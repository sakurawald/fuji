package io.github.sakurawald.module.initializer.teleport_warmup;

import io.github.sakurawald.util.MessageUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.server.network.ServerPlayerEntity;

public class TeleportTicket {

    public ServerPlayerEntity player;
    public Position source;
    public Position destination;
    public boolean ready;
    public BossBar bossbar;

    public TeleportTicket(ServerPlayerEntity player, Position source, Position destination, boolean ready) {
        this.player = player;
        this.source = source;
        this.destination = destination;
        this.ready = ready;
        this.bossbar = BossBar.bossBar(MessageUtil.ofComponent(player, "teleport_warmup.bossbar.name"), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        bossbar.addViewer(player);
    }

}
