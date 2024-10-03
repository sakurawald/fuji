package io.github.sakurawald.module.initializer.echo.send_bossbar;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.core.command.argument.wrapper.impl.StringList;
import io.github.sakurawald.core.command.executor.CommandExecutor;
import io.github.sakurawald.core.command.structure.ExtendedCommandSource;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.manager.impl.bossbar.BossBarTicket;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.echo.send_bossbar.structure.SendBossbarTicket;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CommandRequirement(level = 4)
public class SendBossbarInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {

    }

    @CommandNode("send-bossbar")
    private static int sendBossbar(@CommandSource ServerCommandSource source
        , ServerPlayerEntity player
        , Optional<Integer> totalMs
        , Optional<BossBar.Color> color
        , Optional<BossBar.Style> style
        , Optional<StringList> commandList
        , GreedyString title) {

        Text $title = LocaleHelper.getTextByValue(player, title.getValue());
        BossBar.Color $color = color.orElse(BossBar.Color.PURPLE);
        BossBar.Style $style = style.orElse(BossBar.Style.PROGRESS);

        ServerBossBar bossbar = new ServerBossBar($title, $color, $style);

        Integer $totalMs = totalMs.orElse(3000);
        List<ServerPlayerEntity> $player = List.of(player);
        StringList $commandList = commandList.orElse(new StringList(Collections.emptyList()));

        BossBarTicket bossBarTicket = new SendBossbarTicket(bossbar, $totalMs, $player, () -> {
            ExtendedCommandSource extendedCommandSource = ExtendedCommandSource.asConsole(player.getCommandSource());
            CommandExecutor.execute(extendedCommandSource, $commandList.getValue());

            source.sendMessage(Text.of("the bossbar is done"));
        });

        Managers.getBossBarManager().addTicket(bossBarTicket);

        return CommandHelper.Return.SUCCESS;
    }

}
