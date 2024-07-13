package io.github.sakurawald.module.initializer.home;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.HomeModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.common.structure.Position;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import io.github.sakurawald.util.ScheduleUtil;
import lombok.Getter;
import me.lucko.fabric.api.permissions.v0.Options;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@SuppressWarnings("LombokGetterMayBeUsed")

public class HomeInitializer extends ModuleInitializer {


    @Getter
    private final ConfigHandler<HomeModel> data = new ObjectConfigHandler<>("home.json", HomeModel.class);

    public void onInitialize() {
        data.loadFromDisk();
        data.autoSave(ScheduleUtil.CRON_EVERY_MINUTE);
    }

    @Override
    public void onReload() {
        data.loadFromDisk();
        data.autoSave(ScheduleUtil.CRON_EVERY_MINUTE);
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("home")
                        .then(CommandManager.literal("set").then(myHomesArgument().executes(ctx -> $set(ctx, false)).then(literal("override").executes(ctx -> $set(ctx, true)))))
                        .then(CommandManager.literal("tp").then(myHomesArgument().executes(this::$tp)))
                        .then(CommandManager.literal("unset").then(myHomesArgument().executes(this::$unset)))
                        .then(CommandManager.literal("list").executes(this::$list))
        );
    }

    private Map<String, Position> getHomes(ServerPlayerEntity player) {
        String playerName = player.getGameProfile().getName();
        Map<String, Map<String, Position>> homes = data.model().homes;
        homes.computeIfAbsent(playerName, k -> new HashMap<>());
        return homes.get(playerName);
    }

    private int $tp(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            Map<String, Position> name2position = getHomes(player);
            String homeName = StringArgumentType.getString(ctx, "name");
            if (!name2position.containsKey(homeName)) {
                MessageUtil.sendMessage(player, "home.no_found", homeName);
                return 0;
            }

            Position position = name2position.get(homeName);
            position.teleport(player);
            return Command.SINGLE_SUCCESS;
        });
    }

    private int $unset(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            Map<String, Position> name2position = getHomes(player);
            String homeName = StringArgumentType.getString(ctx, "name");
            if (!name2position.containsKey(homeName)) {
                MessageUtil.sendMessage(player, "home.no_found", homeName);
                return 0;
            }

            name2position.remove(homeName);
            MessageUtil.sendMessage(player, "home.unset.success", homeName);
            return Command.SINGLE_SUCCESS;
        });
    }

    private int $set(CommandContext<ServerCommandSource> ctx, boolean override) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            Map<String, Position> name2position = getHomes(player);
            String homeName = StringArgumentType.getString(ctx, "name");
            if (name2position.containsKey(homeName)) {
                if (!override) {
                    MessageUtil.sendMessage(player, "home.set.fail.need_override", homeName);
                    return Command.SINGLE_SUCCESS;
                }
            }

            Optional<Integer> limit = Options.get(ctx.getSource(), "fuji.home.home_limit", Integer::valueOf);
            if (limit.isPresent() && name2position.size() >= limit.get()) {
                MessageUtil.sendMessage(player, "home.set.fail.limit");
                return Command.SINGLE_SUCCESS;
            }

            name2position.put(homeName, Position.of(player));
            MessageUtil.sendMessage(player, "home.set.success", homeName);
            return Command.SINGLE_SUCCESS;
        });
    }

    private int $list(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            MessageUtil.sendMessage(player, "home.list", getHomes(player).keySet());
            return Command.SINGLE_SUCCESS;
        });
    }

    public RequiredArgumentBuilder<ServerCommandSource, String> myHomesArgument() {
        return argument("name", StringArgumentType.string())
                .suggests((context, builder) -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player == null) return builder.buildFuture();

                            Map<String, Position> name2position = getHomes(player);
                            name2position.keySet().forEach(builder::suggest);
                            return builder.buildFuture();
                        }
                );
    }
}
