package io.github.sakurawald.module.initializer.command_warmup.structure;

import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.manager.impl.bossbar.structure.InterruptibleTicket;
import io.github.sakurawald.core.structure.SpatialPose;
import lombok.Getter;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

@Getter
public class CommandWarmupTicket extends InterruptibleTicket {

    private final String command;

    public CommandWarmupTicket(@NotNull ServerPlayerEntity player, CommandWarmupEntry entry) {
        super(new ServerBossBar(LocaleHelper.getTextByKey(player, "command_warmup.bossbar.name", entry.getCommand()), net.minecraft.entity.boss.BossBar.Color.GREEN, net.minecraft.entity.boss.BossBar.Style.PROGRESS)
            , entry.getMs()
            , player
            , SpatialPose.of(player)
            , entry.getInterruptible());

        this.command = entry.getCommand();
    }

    @Override
    protected void onComplete() {
        player.networkHandler.executeCommand(command);
    }

    public static CommandWarmupTicket make(ServerPlayerEntity player, CommandWarmupEntry entry) {
        return new CommandWarmupTicket(player, entry);
    }

}
