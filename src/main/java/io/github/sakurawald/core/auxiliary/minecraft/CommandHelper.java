package io.github.sakurawald.core.auxiliary.minecraft;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import lombok.experimental.UtilityClass;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;

@UtilityClass
public class CommandHelper {

    public static final String UUID = "uuid";

    @SuppressWarnings("unused")
    public static class Return {
        public static final int FAIL = -1;
        public static final int PASS = 0;
        public static final int SUCCESS = 1;
    }

    public static class Suggestion {
        public static <T> @NotNull SuggestionProvider<ServerCommandSource> identifiers(RegistryKey<? extends Registry<T>> registryKey) {
            return (context, builder) -> {
                Registry<T> registry = RegistryHelper.ofRegistry(registryKey);
                Iterator<T> iterator = registry.iterator();
                while (iterator.hasNext()) {
                    T entry;

                    /*
                     * Steps to trigger the following exception:
                     * 1. /world create 1 minecraft:overworld
                     * 2. /world delete fuji:1
                     * 3. /world tp
                     *
                     * Failed to handle packet net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket@72fe7802, suppressing error
                     *  java.lang.NullPointerException: null
                     */
                    try {
                        entry = iterator.next();
                    } catch (NullPointerException e) {
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

        public static int playerOnlyCommand(@NotNull CommandContext<ServerCommandSource> ctx, @NotNull Function<ServerPlayerEntity, Integer> function) {
            ServerPlayerEntity player = ctx.getSource().getPlayer();
            if (player == null) {
                LocaleHelper.sendMessageByKey(ctx.getSource(), "command.player_only");
                return Return.SUCCESS;
            }

            return function.apply(player);
        }

        public static int itemInHandCommand(@NotNull CommandContext<ServerCommandSource> ctx, @NotNull BiFunction<ServerPlayerEntity, ItemStack, Integer> consumer) {
            return playerOnlyCommand(ctx, player -> {
                ItemStack mainHandStack = player.getMainHandStack();
                if (mainHandStack.isEmpty()) {
                    LocaleHelper.sendMessageByKey(player, "item.empty.not_allow");
                    return Return.FAIL;
                }
                return consumer.apply(player, mainHandStack);
            });
        }
    }

}
