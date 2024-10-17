package io.github.sakurawald.module.initializer.tester;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.SneakyThrows;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

@CommandNode("tester")
@CommandRequirement(level = 4)
public class TesterInitializer extends ModuleInitializer {

    private static final Map<String, String> map = new HashMap<>();

    @SneakyThrows(Exception.class)
    @CommandNode("run")
    private static int $run(@CommandSource ServerPlayerEntity player) {

        MutableText first = Text.literal("first").formatted(Formatting.RED);

        MutableText second = Text.literal("second").formatted(Formatting.GREEN);
        first.append(second);

        MutableText third = Text.literal("third").formatted(Formatting.BLUE);
        first.append(third);

        LogUtil.debug("before = {}", first);
        player.sendMessage(first);

        MutableText after = LocaleHelper.replaceText(first, "hi", Text.literal("{good}"));
        LogUtil.debug("after = {}", after);
        player.sendMessage(after);

        return 1;
    }

    @CommandNode("$1 minus $2")
    private static int $2(@CommandSource ServerPlayerEntity player, Integer a, Integer b) {
        player.sendMessage(Text.of(String.valueOf(a - b)));
        return 1;
    }

    @CommandNode("ctx")
    private static int ctx(@CommandSource CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendMessage(Text.of("root"));
        return 1;
    }

    @CommandNode
    private static int root(@CommandSource ServerPlayerEntity player) {
        player.sendMessage(Text.of("root"));
        return 1;
    }
}
