package io.github.sakurawald.module.initializer.echo.send_title;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LanguageHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class SendTitleInitializer extends ModuleInitializer {

    @CommandNode("send-title")
    @CommandRequirement(level = 4)
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

        Component mainTitleComponent = LanguageHelper.getTextByValue(player, $mainTitle).asComponent();
        Component subTitleComponent = LanguageHelper.getTextByValue(player, $subTitle).asComponent();
        Title.Times times = Title.Times.times(Ticks.duration($fadeInTicks), Ticks.duration($stayTicks), Ticks.duration($fadeOutTicks));
        Title title = Title.title(mainTitleComponent, subTitleComponent, times);
        player.showTitle(title);

        LanguageHelper.sendMessageByKey(ctx.getSource(), "operation.success");
        return CommandHelper.Return.SUCCESS;
    }
}
