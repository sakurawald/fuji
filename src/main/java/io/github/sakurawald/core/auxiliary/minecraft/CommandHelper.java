package io.github.sakurawald.core.auxiliary.minecraft;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.UserCache;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@UtilityClass
public class CommandHelper {

    @SuppressWarnings("unused")
    public static class Return {
        public static final int FAIL = -1;
        public static final int PASS = 0;
        public static final int SUCCESS = 1;
    }

    public static class Suggestion {
        public static <T> @NotNull SuggestionProvider<ServerCommandSource> ofRegistryKey(RegistryKey<? extends Registry<T>> registryKey) {
            return (context, builder) -> {
                Registry<T> registry = IdentifierHelper.ofRegistry(registryKey);
                Iterator<T> iterator = registry.iterator();
                while (iterator.hasNext()) {
                    T entry;
                    try {
                        entry = iterator.next();
                    } catch (Exception e) {
                        continue;
                    }

                    Identifier id = registry.getId(entry);
                    builder.suggest(String.valueOf(id));
                }
                return builder.buildFuture();
            };
        }
    }

    public static class Pattern {

        @SneakyThrows
        public static int playerOnlyCommand(@NotNull CommandContext<ServerCommandSource> ctx, @NotNull Function<ServerPlayerEntity, Integer> function) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();
            if (player == null) {
                MessageHelper.sendMessage(ctx.getSource(), "command.player_only");
                return Return.SUCCESS;
            }

            return function.apply(player);
        }

        @SneakyThrows
        public static int itemOnHandCommand(@NotNull CommandContext<ServerCommandSource> ctx, @NotNull BiFunction<ServerPlayerEntity, ItemStack, Integer> consumer) {
            return playerOnlyCommand(ctx, player -> {
                ItemStack mainHandStack = player.getMainHandStack();
                if (mainHandStack.isEmpty()) {
                    MessageHelper.sendMessage(player, "item.empty");
                    return Return.FAIL;
                }
                return consumer.apply(player, mainHandStack);
            });
        }
    }

}
