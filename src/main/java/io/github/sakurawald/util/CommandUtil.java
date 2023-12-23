package io.github.sakurawald.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.seen.GameProfileCacheEx;
import lombok.experimental.UtilityClass;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;

import static net.minecraft.commands.Commands.argument;

@UtilityClass
public class CommandUtil {
    public static RequiredArgumentBuilder<CommandSourceStack, String> offlinePlayerArgument(String argumentName) {
        return argument(argumentName, StringArgumentType.string())
                .suggests((context, builder) -> {
                            GameProfileCache gameProfileCache = Fuji.SERVER.getProfileCache();
                            if (gameProfileCache != null) {
                                ((GameProfileCacheEx) gameProfileCache).fuji$getNames().forEach(builder::suggest);
                            }
                            return builder.buildFuture();
                        }
                );
    }


    public static RequiredArgumentBuilder<CommandSourceStack, String> offlinePlayerArgument() {
        return offlinePlayerArgument("player");
    }

    public static int playerOnlyCommand(CommandContext<CommandSourceStack> ctx, PlayerOnlyCommandFunction function) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) {
            MessageUtil.sendMessage(ctx.getSource(), "command.player_only");
            return Command.SINGLE_SUCCESS;
        }

        return function.run(player);
    }

    @FunctionalInterface
    public interface PlayerOnlyCommandFunction {
        int run(ServerPlayer player);
    }
}
