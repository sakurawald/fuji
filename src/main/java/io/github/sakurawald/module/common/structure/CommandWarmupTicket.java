package io.github.sakurawald.module.common.structure;

import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

@SuppressWarnings("LombokGetterMayBeUsed")
@Getter
public abstract class CommandWarmupTicket extends BossBarTicket{

    private final ServerPlayerEntity player;

    private final String command;

    public CommandWarmupTicket(BossBar bossbar, int totalMS, ServerPlayerEntity player, String command) {
        super(bossbar, totalMS, List.of(player));
        this.player = player;
        this.command = command;
    }

}
