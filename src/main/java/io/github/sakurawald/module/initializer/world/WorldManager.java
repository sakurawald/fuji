package io.github.sakurawald.module.initializer.world;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.sakurawald.core.accessor.SimpleRegistryAccessor;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.structure.Position;
import io.github.sakurawald.core.structure.TeleportTicket;
import io.github.sakurawald.module.initializer.world.accessor.IDimensionOptions;
import io.github.sakurawald.module.initializer.world.structure.MyServerWorld;
import io.github.sakurawald.module.initializer.world.structure.MyWorldProperties;
import io.github.sakurawald.module.initializer.world.structure.VoidWorldGenerationProgressListener;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionTypes;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldManager {

    private static final Set<ServerWorld> deletionQueue = new ReferenceOpenHashSet<>();

    static {
        ServerTickEvents.START_SERVER_TICK.register(server -> tick());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void requestToDeleteWorld(@NotNull ServerWorld world) {
        MinecraftServer server = world.getServer();
        server.submit(() -> {
            deletionQueue.add(world);
        });
    }

    private static void tick() {
        if (!deletionQueue.isEmpty()) {
            deletionQueue.removeIf(WorldManager::tryDeleteWorld);
        }
    }

    private static boolean tryDeleteWorld(@NotNull ServerWorld world) {
        if (isWorldUnloaded(world)) {
            deleteWorld(world);
            return true;
        } else {
            kickPlayers(world);
            return false;
        }
    }

    private static void kickPlayers(@NotNull ServerWorld world) {
        if (world.getPlayers().isEmpty()) {
            return;
        }

        ServerWorld overworld = world.getServer().getOverworld();
        BlockPos spawnPos = overworld.getSpawnPos();

        List<ServerPlayerEntity> players = new ArrayList<>(world.getPlayers());
        for (ServerPlayerEntity player : players) {
            // fix: if the player is inside resource-world while resetting the worlds, then resource worlds will delay its deletion until the player left the resource-world.
            Position from = Position.of(player);
            Position to = new Position(overworld, spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5, 0, 0);

            TeleportTicket teleportTicket = TeleportTicket.ofInstantTicket(player, from, to);
            Managers.getBossBarManager().addTicket(teleportTicket);
        }
    }

    private static boolean isWorldUnloaded(@NotNull ServerWorld world) {
        return world.getPlayers().isEmpty() && world.getChunkManager().getLoadedChunkCount() <= 0;
    }

    private static SimpleRegistry<DimensionOptions> getDimensionRegistry(@NotNull MinecraftServer server) {
        DynamicRegistryManager registryManager = server.getCombinedDynamicRegistries().getCombinedRegistryManager();
        return (SimpleRegistry<DimensionOptions>) registryManager.get(RegistryKeys.DIMENSION);
    }

    private static void deleteWorld(@NotNull ServerWorld world) {
        MinecraftServer server = world.getServer();

        RegistryKey<World> dimensionKey = world.getRegistryKey();
        if (server.worlds.remove(dimensionKey, world)) {
            ServerWorldEvents.UNLOAD.invoker().onWorldUnload(server, world);
            SimpleRegistry<DimensionOptions> dimensionsRegistry = getDimensionRegistry(server);
            SimpleRegistryAccessor.remove(dimensionsRegistry, dimensionKey.getValue());
            File worldDirectory = server.session.getWorldDirectory(dimensionKey).toFile();
            cleanFiles(worldDirectory);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void cleanFiles(@NotNull File file) {
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return;
            for (File child : files) {
                if (child.isDirectory()) {
                    cleanFiles(child);
                } else {
                    child.delete();
                }
            }
        }
    }

    /**
     * To avoid share the same reference of the vanilla minecraft default `DimensionOptions` instance,
     * we must create new instance.
     *
     */
    @SneakyThrows
    private static @NotNull DimensionOptions makeDimensionOptions(@NotNull Registry<DimensionOptions> registry, Identifier dimensionTypeIdentifier) {
        DimensionOptions template = registry.get(dimensionTypeIdentifier);
        if (template == null) {
            throw new SimpleCommandExceptionType(Text.of("The dimension type %s can't be used as template.".formatted(dimensionTypeIdentifier))).create();
        }

        return new DimensionOptions(template.dimensionTypeEntry(), template.chunkGenerator());
    }

    public static void requestToCreateWorld(@NotNull MinecraftServer server, Identifier dimensionIdentifier, @NotNull Identifier dimenstionTypeIdentifier, long seed) {
        /* create the world */
        // note: we use the same WorldData from OVERWORLD
        MyWorldProperties worldProperties = new MyWorldProperties(server.getSaveProperties(), seed);
        RegistryKey<World> worldRegistryKey = RegistryKey.of(RegistryKeys.WORLD, dimensionIdentifier);

        DynamicRegistryManager registryManager = server.getCombinedDynamicRegistries().getCombinedRegistryManager();
        Registry<DimensionOptions> registry = registryManager.get(RegistryKeys.DIMENSION);
        DimensionOptions dimensionOptions = makeDimensionOptions(registry, dimenstionTypeIdentifier);

        ServerWorld world;
        try {
            world = new MyServerWorld(server,
                    Util.getMainWorkerExecutor(),
                    server.session,
                    worldProperties,
                    worldRegistryKey,
                    dimensionOptions,
                    VoidWorldGenerationProgressListener.INSTANCE,
                    false,
                    BiomeAccess.hashSeed(seed),
                    ImmutableList.of(),
                    true,
                    null);
        } catch (Exception e) {
            LogUtil.warn("Failed to create world: worldId = {}, dimensionTypeId = {}, error = {}", dimensionIdentifier, dimenstionTypeIdentifier, e);
            return;
        }

        if (dimenstionTypeIdentifier.equals(DimensionTypes.THE_END_ID)) {
            world.setEnderDragonFight(new EnderDragonFight(world, world.getSeed(), EnderDragonFight.Data.DEFAULT));
        }

        /* register the world */
        ((IDimensionOptions) (Object) dimensionOptions).fuji$setSaveProperties(false);

        SimpleRegistry<DimensionOptions> dimensionOptionsRegistry = (SimpleRegistry<DimensionOptions>) registryManager.get(RegistryKeys.DIMENSION);
        boolean original = ((SimpleRegistryAccessor<?>) dimensionOptionsRegistry).fuji$isFrozen();
        ((SimpleRegistryAccessor<?>) dimensionOptionsRegistry).fuji$setFrozen(false);
        RegistryKey<DimensionOptions> dimensionOptionsRegistryKey = RegistryKey.of(RegistryKeys.DIMENSION, worldRegistryKey.getValue());
        if (!dimensionOptionsRegistry.contains(dimensionOptionsRegistryKey)) {
            dimensionOptionsRegistry.add(dimensionOptionsRegistryKey, dimensionOptions, RegistryEntryInfo.DEFAULT);
        }
        ((SimpleRegistryAccessor<?>) dimensionOptionsRegistry).fuji$setFrozen(original);

        server.worlds.put(world.getRegistryKey(), world);
        ServerWorldEvents.LOAD.invoker().onWorldLoad(server, world);
    }
}
