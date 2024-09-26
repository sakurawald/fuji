package io.github.sakurawald.module.initializer.tester;

import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.SneakyThrows;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@CommandNode("tester")
@CommandRequirement(level = 4)
public class TesterInitializer extends ModuleInitializer {

    @SneakyThrows
    @CommandNode("run")
    private static int $run(@CommandSource ServerPlayerEntity player) {
        player.sendMessage(Text.of("run"));

//        ServerBossBar hello = new ServerBossBar(Text.of("Hello"), BossBar.Color.GREEN, BossBar.Style.NOTCHED_12);
//        hello.addPlayer(player);

        MutableText before = Text.literal("first")
            .append(Text.literal("aaabbbcccdddeee").formatted(Formatting.RED))
            .append("third");

        LogUtil.debug("before = {}", before);
        MutableText text = Text.literal("good")
            .setStyle(Style.EMPTY
                .withFormatting(Formatting.GREEN)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("hello")))
            );
//        Text after = LocaleHelper.replaceText(before, "b", text, "c", Text.literal("C"));
        Text after = LocaleHelper.replaceText(before, "b", text);
        LogUtil.debug("after = {}", after);

        player.sendMessage(before);
        player.sendMessage(after);

        return 1;
    }

    @CommandNode("$1 minus $2")
    private static int $2(@CommandSource ServerPlayerEntity player, Integer a, Integer b) {
        player.sendMessage(Text.of(String.valueOf(a - b)));
        return 1;
    }

    @CommandNode
    private static int root(@CommandSource ServerPlayerEntity player) {
        player.sendMessage(Text.of("root"));
        return 1;
    }

}
