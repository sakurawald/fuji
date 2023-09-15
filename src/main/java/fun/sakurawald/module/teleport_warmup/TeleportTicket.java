package fun.sakurawald.module.teleport_warmup;

import fun.sakurawald.config.ConfigManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

public class TeleportTicket {

    public ServerPlayer player;
    public Position source;
    public Position destination;
    public boolean ready;
    public ServerBossEvent bossbar;

    public TeleportTicket(ServerPlayer player, Position source, Position destination, boolean ready) {
        this.player = player;
        this.source = source;
        this.destination = destination;
        this.ready = ready;
        this.bossbar = new ServerBossEvent(Component.nullToEmpty(ConfigManager.configWrapper.instance().modules.teleport_warmup.bossbar_name), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
        bossbar.setProgress(0f);
        bossbar.addPlayer(player);
        bossbar.setVisible(true);
    }

}
