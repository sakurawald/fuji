package io.github.sakurawald.module.initializer.command_toolbox.lore;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.adapter.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class LoreInitializer extends ModuleInitializer {

    @Command("lore unset")
    private int $unset(@CommandSource CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.itemOnHandCommand(ctx, (player, item) -> {
            LoreComponent loreComponent = new LoreComponent(List.of());
            item.set(DataComponentTypes.LORE, loreComponent);
            return CommandHelper.Return.SUCCESS;
        });
    }

    @Command("lore set")
    private int $set(@CommandSource CommandContext<ServerCommandSource> ctx, GreedyString lore) {
        return CommandHelper.Pattern.itemOnHandCommand(ctx, (player, item) -> {
            List<Text> texts = MessageHelper.ofTextList(player, false, lore.getString());
            LoreComponent loreComponent = new LoreComponent(texts);
            item.set(DataComponentTypes.LORE, loreComponent);
            return CommandHelper.Return.SUCCESS;
        });
    }
}
