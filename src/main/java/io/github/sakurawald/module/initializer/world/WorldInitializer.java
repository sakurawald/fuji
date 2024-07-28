package io.github.sakurawald.module.initializer.world;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.WorldModel;
import io.github.sakurawald.module.common.structure.TeleportSetup;
import io.github.sakurawald.module.common.structure.random_teleport.RandomTeleport;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.world.structure.DimensionEntry;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.IdentifierHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import lombok.Getter;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.RandomSeed;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

/*
 * RegistryKeys.DIMENSION_TYPE only returns registered `dimension type`.
 *
 * To list multi dimensions, use `RegistryKeys.DIMENSION`.
 * DimensionArgumentType.dimension() is equals to RegistryKeys.DIMENSION, but the DimensionArgumentType()'s suggestion will not suggest new added dimension types.
 *
 * The `RegistryKeys.WORLD` and `RegistryKeys.DIMENSION` can be cast to each other.
 * public static final RegistryKey<Registry<World>> WORLD = RegistryKeys.of("dimension");
 * public static final RegistryKey<Registry<DimensionOptions>> DIMENSION = RegistryKeys.of("dimension");
 */

@SuppressWarnings("LombokGetterMayBeUsed")
public class WorldInitializer extends ModuleInitializer {

    @Getter
    private final ConfigHandler<WorldModel> storage = new ObjectConfigHandler<>("world.json", WorldModel.class);

    @Override
    public void onInitialize() {
        storage.loadFromDisk();
        ServerLifecycleEvents.SERVER_STARTED.register(this::loadWorlds);
    }

    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("world")
                        .then(literal("create").requires(source -> source.hasPermissionLevel(4))
                                .then(CommandHelper.Argument.name()
                                        .then(CommandHelper.Argument.identifier().suggests(CommandHelper.Suggestion.dimensionType()).executes(this::$create))))
                        .then(literal("tp")
                                .then(CommandHelper.Argument.dimension().executes(this::$tp)))
                        .then(literal("delete").requires(source -> source.hasPermissionLevel(4))
                                .then(CommandHelper.Argument.dimension().executes(this::$delete)))
                        .then(literal("reset").requires(source -> source.hasPermissionLevel(4))
                                .then(CommandHelper.Argument.dimension().executes(this::$reset)))
        );
    }

    public void loadWorlds(@NotNull MinecraftServer server) {
        for (DimensionEntry dimensionEntry : storage.model().dimension_list) {
            if (!dimensionEntry.isEnable()) continue;

            Identifier dimensionType = Identifier.of(dimensionEntry.getDimensionType());
            Identifier dimension = Identifier.of(dimensionEntry.getDimension());
            long seed = dimensionEntry.getSeed();
            WorldManager.requestToCreateWorld(server, dimension, dimensionType, seed);
        }
    }

    private int $tp(@NotNull CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, player -> {
            ServerWorld world = CommandHelper.Argument.dimension(ctx);

            MessageHelper.sendActionBar(player, "world.dimension.tp.tip");
            Optional<TeleportSetup> tpSetup = TeleportSetup.of(world);
            if (tpSetup.isEmpty()) {
                MessageHelper.sendMessage(player, "rtp.dimension.disallow", IdentifierHelper.ofString(world));
                return CommandHelper.Return.FAIL;
            }
            RandomTeleport.request(player, tpSetup.get(), null);

            return CommandHelper.Return.SUCCESS;
        });
    }

    @SneakyThrows
    private int $create(@NotNull CommandContext<ServerCommandSource> ctx) {
        Identifier dimensionTypeIdentifier = Identifier.of(CommandHelper.Argument.identifier(ctx));
        String name = CommandHelper.Argument.name(ctx);
        String FUJI_DIMENSION_NAMESPACE = "fuji";
        Identifier dimensionIdentifier = Identifier.of(FUJI_DIMENSION_NAMESPACE, name);

        if (IdentifierHelper.ofRegistry(RegistryKeys.DIMENSION).containsId(dimensionIdentifier)) {
            MessageHelper.sendMessage(ctx.getSource(), "world.dimension.exist");
            return CommandHelper.Return.FAIL;
        }

        long seed = RandomSeed.getSeed();
        WorldManager.requestToCreateWorld(ServerHelper.getDefaultServer(), dimensionIdentifier, dimensionTypeIdentifier, seed);

        storage.model().dimension_list.add(new DimensionEntry(true, dimensionIdentifier.toString(), dimensionTypeIdentifier.toString(), seed));
        storage.saveToDisk();

        MessageHelper.sendBroadcast("world.dimension.created", dimensionIdentifier);
        return CommandHelper.Return.SUCCESS;
    }


    @SneakyThrows
    private int $delete(@NotNull CommandContext<ServerCommandSource> ctx) {
        ServerWorld world = CommandHelper.Argument.dimension(ctx);
        String identifier = IdentifierHelper.ofString(world);
        if (Configs.configHandler.model().modules.world.blacklist.dimension_list.contains(identifier)) {
            MessageHelper.sendMessage(ctx.getSource(), "world.dimension.blacklist", identifier);
            return CommandHelper.Return.FAIL;
        }

        WorldManager.requestToDeleteWorld(world);

        Optional<DimensionEntry> first = storage.model().dimension_list.stream().filter(o -> o.getDimension().equals(identifier)).findFirst();
        if (first.isEmpty()) {
            MessageHelper.sendMessage(ctx.getSource(), "world.dimension.not_exist");
            return CommandHelper.Return.FAIL;
        }
        storage.model().dimension_list.remove(first.get());
        storage.saveToDisk();

        MessageHelper.sendBroadcast("world.dimension.deleted", identifier);
        return CommandHelper.Return.SUCCESS;
    }

    @SneakyThrows
    private int $reset(@NotNull CommandContext<ServerCommandSource> ctx) {
        // draw seed and save
        ServerWorld world = CommandHelper.Argument.dimension(ctx);
        String identifier = IdentifierHelper.ofString(world);
        if (Configs.configHandler.model().modules.world.blacklist.dimension_list.contains(identifier)) {
            MessageHelper.sendMessage(ctx.getSource(), "world.dimension.blacklist",identifier);
            return CommandHelper.Return.FAIL;
        }

        Optional<DimensionEntry> first = storage.model().dimension_list.stream().filter(o -> o.getDimension().equals(identifier)).findFirst();
        if (first.isEmpty()) {
            MessageHelper.sendMessage(ctx.getSource(), "world.dimension.not_exist");
            return CommandHelper.Return.FAIL;
        }
        first.get().setSeed(RandomSeed.getSeed());
        storage.saveToDisk();

        MessageHelper.sendBroadcast("world.dimension.reset", identifier);

        // just delete it
        WorldManager.requestToDeleteWorld(world);
        WorldManager.requestToCreateWorld(ServerHelper.getDefaultServer(), Identifier.of(identifier), Identifier.of(first.get().getDimensionType()), first.get().getSeed());

        return CommandHelper.Return.SUCCESS;
    }
}
