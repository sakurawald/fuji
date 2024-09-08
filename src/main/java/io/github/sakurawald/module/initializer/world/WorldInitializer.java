package io.github.sakurawald.module.initializer.world;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.IdentifierHelper;
import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.Dimension;
import io.github.sakurawald.core.command.argument.wrapper.impl.DimensionType;
import io.github.sakurawald.core.command.exception.SnackException;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.config.handler.abst.ConfigHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.world.config.model.WorldModel;
import io.github.sakurawald.module.initializer.world.structure.DimensionEntry;
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
@CommandRequirement(level = 4)
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

    @CommandNode("world tp")
    private int $tp(@CommandSource ServerPlayerEntity player, Dimension dimension) {
        ServerWorld world = dimension.getValue();

        BlockPos spawnPos = world.getSpawnPos();

        player.teleport(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), player.getYaw(), player.getPitch());
        return CommandHelper.Return.SUCCESS;
    }

    @SneakyThrows
    @CommandNode("world create")
    private int $create(@CommandSource CommandContext<ServerCommandSource> ctx, String name,
                        Optional<Long> seed, DimensionType dimensionType) {
        Identifier dimensionTypeIdentifier = Identifier.of(dimensionType.getValue());
        String FUJI_DIMENSION_NAMESPACE = "fuji";
        Identifier dimensionIdentifier = Identifier.of(FUJI_DIMENSION_NAMESPACE, name);

        if (IdentifierHelper.ofRegistry(RegistryKeys.DIMENSION).containsId(dimensionIdentifier)) {
            MessageHelper.sendMessageByKey(ctx.getSource(), "world.dimension.exist");
            return CommandHelper.Return.FAIL;
        }

        long $seed = seed.orElse(RandomSeed.getSeed());
        WorldManager.requestToCreateWorld(ServerHelper.getDefaultServer(), dimensionIdentifier, dimensionTypeIdentifier, $seed);

        storage.model().dimension_list.add(new DimensionEntry(true, dimensionIdentifier.toString(), dimensionTypeIdentifier.toString(), $seed));
        storage.saveToDisk();

        MessageHelper.sendBroadcastByKey("world.dimension.created", dimensionIdentifier);
        return CommandHelper.Return.SUCCESS;
    }


    @SneakyThrows
    @CommandNode("world delete")
    private int $delete(@CommandSource CommandContext<ServerCommandSource> ctx, Dimension dimension) {
        ServerWorld world = dimension.getValue();

        String identifier = IdentifierHelper.ofString(world);
        checkBlacklist(ctx, identifier);

        WorldManager.requestToDeleteWorld(world);

        Optional<DimensionEntry> first = storage.model().dimension_list.stream().filter(o -> o.getDimension().equals(identifier)).findFirst();
        if (first.isEmpty()) {
            MessageHelper.sendMessageByKey(ctx.getSource(), "world.dimension.not_exist");
            return CommandHelper.Return.FAIL;
        }
        storage.model().dimension_list.remove(first.get());
        storage.saveToDisk();

        MessageHelper.sendBroadcastByKey("world.dimension.deleted", identifier);
        return CommandHelper.Return.SUCCESS;
    }

    private void checkBlacklist(CommandContext<ServerCommandSource> ctx, String identifier) {
        if (Configs.configHandler.model().modules.world.blacklist.dimension_list.contains(identifier)) {
            MessageHelper.sendMessageByKey(ctx.getSource(), "world.dimension.blacklist", identifier);
            throw new SnackException();
        }
    }

    @SneakyThrows
    @CommandNode("world reset")
    private int $reset(@CommandSource CommandContext<ServerCommandSource> ctx, Optional<Boolean> useTheSameSeed, Dimension dimension) {
        // draw seed and save
        ServerWorld world = dimension.getValue();
        String identifier = IdentifierHelper.ofString(world);
        checkBlacklist(ctx, identifier);

        Optional<DimensionEntry> first = storage.model().dimension_list.stream().filter(o -> o.getDimension().equals(identifier)).findFirst();
        if (first.isEmpty()) {
            MessageHelper.sendMessageByKey(ctx.getSource(), "world.dimension.not_exist");
            return CommandHelper.Return.FAIL;
        }
        // just delete it
        WorldManager.requestToDeleteWorld(world);

        Boolean $useTheSameSeed = useTheSameSeed.orElse(false);

        long newSeed = $useTheSameSeed ? first.get().getSeed() : RandomSeed.getSeed();
        WorldManager.requestToCreateWorld(ServerHelper.getDefaultServer(), Identifier.of(identifier), Identifier.of(first.get().getDimensionType()), newSeed);

        // save the new seed
        first.get().setSeed(newSeed);
        storage.saveToDisk();

        MessageHelper.sendBroadcastByKey("world.dimension.reset", identifier);
        return CommandHelper.Return.SUCCESS;
    }
}
