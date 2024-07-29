package io.github.sakurawald.module.initializer.home;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.HomeModel;
import io.github.sakurawald.module.common.structure.Position;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.common.manager.scheduler.ScheduleManager;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.PermissionHelper;
import lombok.Getter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import static net.minecraft.server.command.CommandManager.literal;

@SuppressWarnings("LombokGetterMayBeUsed")
public class HomeInitializer extends ModuleInitializer {

    @Getter
    private final ConfigHandler<HomeModel> data = new ObjectConfigHandler<>("home.json", HomeModel.class);

    public void onInitialize() {
        data.loadFromDisk();
        data.setAutoSaveJob(ScheduleManager.CRON_EVERY_MINUTE);
    }

    @Override
    public void onReload() {
        data.loadFromDisk();
        data.setAutoSaveJob(ScheduleManager.CRON_EVERY_MINUTE);
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(
                literal("home")
                        .then(literal("set").then(myHomesArgument().executes(ctx -> $set(ctx, false)).then(literal("override").executes(ctx -> $set(ctx, true)))))
                        .then(literal("tp").then(myHomesArgument().executes(this::$tp)))
                        .then(literal("unset").then(myHomesArgument().executes(this::$unset)))
                        .then(literal("list").executes(this::$list))
        );
    }

    private Map<String, Position> getHomes(@NotNull ServerPlayerEntity player) {
        String playerName = player.getGameProfile().getName();
        Map<String, Map<String, Position>> homes = data.model().homes;
        homes.computeIfAbsent(playerName, k -> new HashMap<>());
        return homes.get(playerName);
    }

    private int $tp(@NotNull CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, player -> {
            Map<String, Position> name2position = getHomes(player);
            String homeName = CommandHelper.Argument.name(ctx);
            if (!name2position.containsKey(homeName)) {
                MessageHelper.sendMessage(player, "home.no_found", homeName);
                return 0;
            }

            Position position = name2position.get(homeName);
            position.teleport(player);
            return CommandHelper.Return.SUCCESS;
        });
    }

    private int $unset(@NotNull CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, player -> {
            Map<String, Position> name2position = getHomes(player);
            String homeName = CommandHelper.Argument.name(ctx);
            if (!name2position.containsKey(homeName)) {
                MessageHelper.sendMessage(player, "home.no_found", homeName);
                return 0;
            }

            name2position.remove(homeName);
            MessageHelper.sendMessage(player, "home.unset.success", homeName);
            return CommandHelper.Return.SUCCESS;
        });
    }

    private int $set(@NotNull CommandContext<ServerCommandSource> ctx, boolean override) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, player -> {
            Map<String, Position> name2position = getHomes(player);
            String homeName = CommandHelper.Argument.name(ctx);
            if (name2position.containsKey(homeName)) {
                if (!override) {
                    MessageHelper.sendMessage(player, "home.set.fail.need_override", homeName);
                    return CommandHelper.Return.SUCCESS;
                }
            }

            Optional<Integer> limit = PermissionHelper.getMeta(player, "fuji.home.home_limit", Integer::valueOf);
            if (limit.isPresent() && name2position.size() >= limit.get()) {
                MessageHelper.sendMessage(player, "home.set.fail.limit");
                return CommandHelper.Return.SUCCESS;
            }

            name2position.put(homeName, Position.of(player));
            MessageHelper.sendMessage(player, "home.set.success", homeName);
            return CommandHelper.Return.SUCCESS;
        });
    }

    private int $list(@NotNull CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, player -> {
            MessageHelper.sendMessage(player, "home.list", getHomes(player).keySet());
            return CommandHelper.Return.SUCCESS;
        });
    }

    public RequiredArgumentBuilder<ServerCommandSource, String> myHomesArgument() {
        return CommandHelper.Argument.name()
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
