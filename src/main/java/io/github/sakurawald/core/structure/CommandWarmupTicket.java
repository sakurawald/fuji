package io.github.sakurawald.core.structure;

import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.manager.impl.bossbar.BossBarTicket;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class CommandWarmupTicket extends BossBarTicket {

    private final @NotNull ServerPlayerEntity player;

    private final String command;

    public CommandWarmupTicket(BossBar bossbar, int totalMS, @NotNull ServerPlayerEntity player, String command) {
        super(bossbar, totalMS, List.of(player));
        this.player = player;
        this.command = command;
    }

    @Override
    public void onComplete() {
        player.networkHandler.executeCommand(command);
    }

    public static CommandWarmupTicket of(ServerPlayerEntity player, String command, int ms) {
        BossBar bossbar = BossBar.bossBar(LanguageHelper.getTextByKey(player, "command_warmup.bossbar.name", command), 0f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);
        return new CommandWarmupTicket(bossbar, ms, player, command);
    }

}
