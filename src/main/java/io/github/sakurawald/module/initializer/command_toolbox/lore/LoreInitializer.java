package io.github.sakurawald.module.initializer.command_toolbox.lore;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

public class LoreInitializer extends ModuleInitializer {

    @CommandNode("lore unset")
    private int $unset(@CommandSource CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.itemInHandCommand(ctx, (player, item) -> {
            LoreComponent loreComponent = new LoreComponent(List.of());
            item.set(DataComponentTypes.LORE, loreComponent);
            return CommandHelper.Return.SUCCESS;
        });
    }

    @CommandNode("lore set")
    private int $set(@CommandSource CommandContext<ServerCommandSource> ctx, GreedyString lore) {
        return CommandHelper.Pattern.itemInHandCommand(ctx, (player, item) -> {
            List<Text> texts = LocaleHelper.getTextListByValue(player, lore.getValue());
            LoreComponent loreComponent = new LoreComponent(texts);
            item.set(DataComponentTypes.LORE, loreComponent);
            return CommandHelper.Return.SUCCESS;
        });
    }
}
