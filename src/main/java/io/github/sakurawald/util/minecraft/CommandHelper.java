package io.github.sakurawald.util.minecraft;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.sakurawald.module.common.accessor.GameProfileCacheEx;
import io.github.sakurawald.util.LogUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.UserCache;

import java.util.Iterator;
import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;

@UtilityClass
public class CommandHelper {

    public static class Return {
        public static final int FAIL = -1;
        public static final int PASS = 0;
        public static final int SUCCESS = 1;
    }

    public static class Argument {
        public static final String ARGUMENT_NAME_DIMENSION = "dimension";
        public static final String ARGUMENT_NAME_ENTITY = "entity";
        public static final String ARGUMENT_NAME_PLAYER = "player";
        public static final String ARGUMENT_NAME_REST = "rest";
        public static final String ARGUMENT_NAME_NAME = "name";
        public static final String ARGUMENT_NAME_IDENTIFIER = "identifier";
        public static final String ARGUMENT_NAME_MESSAGE = "message";

        public static RequiredArgumentBuilder<ServerCommandSource, String> offlinePlayer(String argumentName) {
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

        public static RequiredArgumentBuilder<ServerCommandSource, String> offlinePlayer() {
            return offlinePlayer(ARGUMENT_NAME_PLAYER);
        }

        public static String offlinePlayer(CommandContext<ServerCommandSource> ctx) {
            return string(ctx, ARGUMENT_NAME_NAME);
        }

        public static RequiredArgumentBuilder<ServerCommandSource, EntitySelector> player() {
            return argument(ARGUMENT_NAME_PLAYER, EntityArgumentType.player());
        }

        public static ServerPlayerEntity player(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
            return EntityArgumentType.getPlayer(ctx, ARGUMENT_NAME_PLAYER);
        }

        public static RequiredArgumentBuilder<ServerCommandSource, String> rest() {
            return argument(ARGUMENT_NAME_REST, StringArgumentType.greedyString());
        }

        public static String string(CommandContext<ServerCommandSource> ctx, String argumentName) {
            return StringArgumentType.getString(ctx, argumentName);
        }

        public static String rest(CommandContext<ServerCommandSource> ctx) {
            return string(ctx, ARGUMENT_NAME_REST);
        }

        public static RequiredArgumentBuilder<ServerCommandSource, String> string(String argumentName) {
            return argument(argumentName, StringArgumentType.string());
        }

        public static RequiredArgumentBuilder<ServerCommandSource, String> name() {
            return string(ARGUMENT_NAME_NAME);
        }

        public static String name(CommandContext<ServerCommandSource> ctx) {
            return string(ctx, ARGUMENT_NAME_NAME);
        }

        public static RequiredArgumentBuilder<ServerCommandSource, String> identifier() {
            return argument(ARGUMENT_NAME_IDENTIFIER, StringArgumentType.greedyString());
        }

        public static String identifier(CommandContext<ServerCommandSource> ctx) {
            return string(ctx, ARGUMENT_NAME_IDENTIFIER);
        }

        public static RequiredArgumentBuilder<ServerCommandSource, Identifier> dimension() {
            /*
             The DimensionArgumentType.dimension() will not suggest the new registered dimension types.
             Each time the server started, the dimensions will be shared with client and server.
             */
            return argument(ARGUMENT_NAME_DIMENSION, DimensionArgumentType.dimension()).suggests(CommandHelper.Suggestion.dimension());
        }

        public static ServerWorld dimension(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
            return DimensionArgumentType.getDimensionArgument(ctx, ARGUMENT_NAME_DIMENSION);
        }

    }

    public static class Suggestion {

        public static <T> SuggestionProvider<ServerCommandSource> ofRegistryKey(RegistryKey<? extends Registry<T>> registryKey) {
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

        public static SuggestionProvider<ServerCommandSource> dimension() {
            return ofRegistryKey(RegistryKeys.DIMENSION);
        }

        public static SuggestionProvider<ServerCommandSource> dimensionType() {
            return ofRegistryKey(RegistryKeys.DIMENSION_TYPE);
        }
    }

    @SneakyThrows
    public static int playerOnlyCommand(CommandContext<ServerCommandSource> ctx, PlayerFunction consumer) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player == null) {
            MessageHelper.sendMessage(ctx.getSource(), "command.player_only");
            return CommandHelper.Return.SUCCESS;
        }

        return consumer.run(player);
    }

    public static int acceptEntity(CommandContext<ServerCommandSource> ctx, EntityFunction function) {
        Entity entity;
        try {
            entity = EntityArgumentType.getEntity(ctx, Argument.ARGUMENT_NAME_ENTITY);
        } catch (Exception e) {
            MessageHelper.sendMessage(ctx.getSource(), "entity.no_found");
            return CommandHelper.Return.SUCCESS;
        }

        return function.run(entity);
    }

    public static int acceptPlayer(CommandContext<ServerCommandSource> ctx, Function<ServerPlayerEntity, Integer> function) {
        ServerPlayerEntity player;
        try {
            player = EntityArgumentType.getPlayer(ctx, Argument.ARGUMENT_NAME_PLAYER);
        } catch (Exception e) {
            MessageHelper.sendMessage(ctx.getSource(), "player.no_found");
            return CommandHelper.Return.SUCCESS;
        }

        return function.apply(player);
    }

    @FunctionalInterface
    public interface PlayerFunction {
        int run(ServerPlayerEntity player) throws CommandSyntaxException;
    }

    @FunctionalInterface
    public interface EntityFunction {
        int run(Entity entity);
    }

}
