package io.github.sakurawald.module.initializer.tester;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.NbtHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;


@Command("tester")
@CommandPermission(level = 4)
public class TesterInitializer extends ModuleInitializer {

    void registerStore() {
        Placeholders.register(Identifier.of("fuji", "store"), (ctx, args) -> {
            if (args.isEmpty()) {
                return PlaceholderResult.invalid();

            }

            /**
             * building block -> set, get, if, recur, arithmetic, boolean
             * - %fuji:store {name} {value}%
             * - (set {name} {value})
             * - (get {name})
             * - (cmd {cmd})
             * - (eval {list})
             */

            return null;
        });
    }

    @Command("attachment")
    int attachment(@CommandSource CommandContext<ServerCommandSource> ctx, Optional<String> fileName, GreedyString jsonPath) {

        return 1;
    }

    @Command("run")
    @CommandPermission(level = 0)
    private static int $run(@CommandSource CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();
        MinecraftServer server = player.server;

        CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();

        try {
//            int execute = dispatcher.execute("give SakuraWald apple 1", player.getCommandSource().withLevel(4));
            int execute = dispatcher.execute("give SakuraWald apple 1", player.getCommandSource().withLevel(4));
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        return -1;
    }

}
