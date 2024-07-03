package io.github.sakurawald.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.common.structure.GameProfileCacheEx;
import lombok.experimental.UtilityClass;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.UserCache;

import static net.minecraft.server.command.CommandManager.argument;

@UtilityClass
public class CommandUtil {
    public static RequiredArgumentBuilder<ServerCommandSource, String> offlinePlayerArgument(String argumentName) {
        return argument(argumentName, StringArgumentType.string())
                .suggests((context, builder) -> {
                            UserCache gameProfileCache = Fuji.SERVER.getUserCache();
                            if (gameProfileCache != null) {
                                ((GameProfileCacheEx) gameProfileCache).fuji$getNames().forEach(builder::suggest);
                            }
                            return builder.buildFuture();
                        }
                );
    }


    public static RequiredArgumentBuilder<ServerCommandSource, String> offlinePlayerArgument() {
        return offlinePlayerArgument("player");
    }

    public static int playerOnlyCommand(CommandContext<ServerCommandSource> ctx, PlayerOnlyCommandFunction function) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player == null) {
            MessageUtil.sendMessage(ctx.getSource(), "command.player_only");
            return Command.SINGLE_SUCCESS;
        }

        return function.run(player);
    }

    @FunctionalInterface
    public interface PlayerOnlyCommandFunction {
        int run(ServerPlayerEntity player);
    }
}
