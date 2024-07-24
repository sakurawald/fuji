package io.github.sakurawald.util.minecraft;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.common.accessor.GameProfileCacheEx;
import lombok.experimental.UtilityClass;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.UserCache;

import static net.minecraft.server.command.CommandManager.argument;

@UtilityClass
public class CommandHelper {

    public static class Return {
        public static final int ERROR = 0;
        public static final int SUCCESS = 1;

    }

    public static class Argument {
        public static final String ARGUMENT_NAME_DIMENSION = "dimension";
        public static final String ARGUMENT_NAME_ENTITY = "entity";
        public static final String ARGUMENT_NAME_PLAYER = "player";

        public static RequiredArgumentBuilder<ServerCommandSource, String> offlinePlayerArgument(String argumentName) {
            return argument(argumentName, StringArgumentType.string())
                    .suggests((context, builder) -> {
                                UserCache gameProfileCache = ServerHelper.getDefaultServer().getUserCache();
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
    }


    public static int playerOnlyCommand(CommandContext<ServerCommandSource> ctx, PlayerOnlyCommandFunction function) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player == null) {
            MessageHelper.sendMessage(ctx.getSource(), "command.player_only");
            return CommandHelper.Return.SUCCESS;
        }

        return function.run(player);
    }

    public static int acceptEntity(CommandContext<ServerCommandSource> ctx, AcceptEntityFunction function) {
        Entity entity;
        try {
            entity = EntityArgumentType.getEntity(ctx, Argument.ARGUMENT_NAME_ENTITY);
        } catch (Exception e) {
            MessageHelper.sendMessage(ctx.getSource(), "entity.no_found");
            return CommandHelper.Return.SUCCESS;
        }

        return function.run(entity);
    }

    public static int acceptPlayer(CommandContext<ServerCommandSource> ctx, AcceptPlayerFunction function) {
        ServerPlayerEntity player;
        try {
            player = EntityArgumentType.getPlayer(ctx, Argument.ARGUMENT_NAME_PLAYER);
        } catch (Exception e) {
            MessageHelper.sendMessage(ctx.getSource(), "player.no_found");
            return CommandHelper.Return.SUCCESS;
        }

        return function.run(player);
    }

    @FunctionalInterface
    public interface PlayerOnlyCommandFunction {
        int run(ServerPlayerEntity player);
    }

    @FunctionalInterface
    public interface AcceptEntityFunction {
        int run(Entity entity);
    }

    @FunctionalInterface
    public interface AcceptPlayerFunction {
        int run(ServerPlayerEntity player);
    }
}
