package io.github.sakurawald.module.initializer.command_toolbox.send_title;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class SendTitleInitializer extends ModuleInitializer {

    @CommandNode("send-title")
    @CommandPermission(level = 4)
    int sendTitle(@CommandSource CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player
            , Optional<String> mainTitle
            , Optional<String> subTitle
            , Optional<Integer> fadeInTicks
            , Optional<Integer> stayTicks
            , Optional<Integer> fadeOutTicks
    ) {

        String $mainTitle = mainTitle.orElse("main title");
        String $subTitle = subTitle.orElse("");
        int $fadeInTicks = fadeInTicks.orElse(10);
        int $stayTicks = stayTicks.orElse(70);
        int $fadeOutTicks = fadeOutTicks.orElse(20);

        Component mainTitleComponent = MessageHelper.ofComponent(player, false, $mainTitle);
        Component subTitleComponent = MessageHelper.ofComponent(player, false, $subTitle);
        Title.Times times = Title.Times.times(Ticks.duration($fadeInTicks), Ticks.duration($stayTicks), Ticks.duration($fadeOutTicks));
        Title title = Title.title(mainTitleComponent, subTitleComponent, times);
        player.showTitle(title);

        MessageHelper.sendMessage(ctx.getSource(), "operation.success");
        return CommandHelper.Return.SUCCESS;
    }
}
