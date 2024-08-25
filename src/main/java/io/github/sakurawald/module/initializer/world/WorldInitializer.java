package io.github.sakurawald.module.initializer.world;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.argument.wrapper.Dimension;
import io.github.sakurawald.command.argument.wrapper.DimensionType;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.handler.interfaces.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.module.initializer.world.config.model.WorldModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.world.structure.DimensionEntry;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.IdentifierHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.auxiliary.minecraft.ServerHelper;
import lombok.Getter;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.RandomSeed;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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

@Getter
@CommandPermission(level = 4)
public class WorldInitializer extends ModuleInitializer {

    private final ConfigHandler<WorldModel> storage = new ObjectConfigHandler<>("world.json", WorldModel.class);

    @Override
    public void onInitialize() {
        storage.loadFromDisk();
        ServerLifecycleEvents.SERVER_STARTED.register(this::loadWorlds);
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

    @Command("world tp")
    private int $tp(@CommandSource ServerPlayerEntity player, Dimension dimension) {
        ServerWorld world = dimension.getWorld();

        BlockPos spawnPos = world.getSpawnPos();

        player.teleport(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), player.getYaw(), player.getPitch());
        return CommandHelper.Return.SUCCESS;
    }

    @SneakyThrows
    @Command("world create")
    private int $create(@CommandSource CommandContext<ServerCommandSource> ctx, String name, DimensionType dimensionType) {
        Identifier dimensionTypeIdentifier = Identifier.of(dimensionType.getIdentifier());
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
    @Command("world delete")
    private int $delete(@CommandSource CommandContext<ServerCommandSource> ctx, Dimension dimension) {
        ServerWorld world = dimension.getWorld();

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
    @Command("world reset")
    private int $reset(@CommandSource CommandContext<ServerCommandSource> ctx, Dimension dimension) {
        // draw seed and save
        ServerWorld world = dimension.getWorld();
        String identifier = IdentifierHelper.ofString(world);
        if (Configs.configHandler.model().modules.world.blacklist.dimension_list.contains(identifier)) {
            MessageHelper.sendMessage(ctx.getSource(), "world.dimension.blacklist", identifier);
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
