package io.github.sakurawald.module.initializer.world;

import com.google.common.collect.ImmutableList;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.common.structure.Position;
import io.github.sakurawald.module.common.accessor.SimpleRegistryMixinInterface;
import io.github.sakurawald.module.common.structure.TeleportTicket;
import io.github.sakurawald.module.initializer.world.interfaces.IDimensionOptions;
import io.github.sakurawald.module.initializer.world.structure.MyServerWorld;
import io.github.sakurawald.module.initializer.world.structure.MyWorldProperties;
import io.github.sakurawald.module.initializer.world.structure.VoidWorldGenerationProgressListener;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.MessageHelper;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionTypes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldManager {

    private static final Set<ServerWorld> deletionQueue = new ReferenceOpenHashSet<>();

    static {
        ServerTickEvents.START_SERVER_TICK.register(server -> tick());
    }

    public static void requestToDeleteWorld(ServerWorld world) {
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

    private static boolean tryDeleteWorld(ServerWorld world) {
        if (isWorldUnloaded(world)) {
            deleteWorld(world);
            return true;
        } else {
            kickPlayers(world);
            return false;
        }
    }

    private static void kickPlayers(ServerWorld world) {
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

    private static boolean isWorldUnloaded(ServerWorld world) {
        return world.getPlayers().isEmpty() && world.getChunkManager().getLoadedChunkCount() <= 0;
    }

    private static SimpleRegistry<DimensionOptions> getDimensionRegistry(MinecraftServer server) {
        DynamicRegistryManager registryManager = server.getCombinedDynamicRegistries().getCombinedRegistryManager();
        return (SimpleRegistry<DimensionOptions>) registryManager.get(RegistryKeys.DIMENSION);
    }

    private static void deleteWorld(ServerWorld world) {
        MinecraftServer server = world.getServer();

        RegistryKey<World> dimensionKey = world.getRegistryKey();
        if (server.worlds.remove(dimensionKey, world)) {
            ServerWorldEvents.UNLOAD.invoker().onWorldUnload(server, world);
            SimpleRegistry<DimensionOptions> dimensionsRegistry = getDimensionRegistry(server);
            SimpleRegistryMixinInterface.remove(dimensionsRegistry, dimensionKey.getValue());
            File worldDirectory = server.session.getWorldDirectory(dimensionKey).toFile();
            cleanFiles(worldDirectory);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void cleanFiles(File file) {
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
    private static DimensionOptions makeDimensionOptions(Registry<DimensionOptions> registry, Identifier dimensionTypeIdentifier)  {
        /*
         note: the vanilla minecraft dimension types (the_nether and the_end) will not register in the Registry<DimensionOptions>,
         so we have to hard-code them.
         */
//        if (DimensionTypes.OVERWORLD_ID.equals(dimensionTypeIdentifier)) return registry.get(DimensionOptions.OVERWORLD);
//        if (DimensionTypes.THE_NETHER_ID.equals(dimensionTypeIdentifier)) return registry.get(DimensionOptions.NETHER);
//        if (DimensionTypes.THE_END_ID.equals(dimensionTypeIdentifier)) {
//            LogUtil.warn("it's end");
//
//            return registry.get(DimensionOptions.END);
//        }

        DimensionOptions template = registry.get(dimensionTypeIdentifier);
        return new DimensionOptions(template.dimensionTypeEntry(), template.chunkGenerator());
    }

    public static void requestToCreateWorld(MinecraftServer server, Identifier dimensionIdentifier, Identifier dimenstionTypeIdentifier, long seed) {
        /* create the world */
        // note: we use the same WorldData from OVERWORLD
        MyWorldProperties resourceWorldProperties = new MyWorldProperties(server.getSaveProperties(), seed);
        RegistryKey<World> worldRegistryKey = RegistryKey.of(RegistryKeys.WORLD, dimensionIdentifier);

        DynamicRegistryManager registryManager = server.getCombinedDynamicRegistries().getCombinedRegistryManager();
        Registry<DimensionOptions> registry = registryManager.get(RegistryKeys.DIMENSION);
        DimensionOptions dimensionOptions = makeDimensionOptions(registry, dimenstionTypeIdentifier);

        ServerWorld world;
        try {
            world = new MyServerWorld(server,
                    Util.getMainWorkerExecutor(),
                    server.session,
                    resourceWorldProperties,
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
        boolean original = ((SimpleRegistryMixinInterface<?>) dimensionOptionsRegistry).fuji$isFrozen();
        ((SimpleRegistryMixinInterface<?>) dimensionOptionsRegistry).fuji$setFrozen(false);
        RegistryKey<DimensionOptions> dimensionOptionsRegistryKey = RegistryKey.of(RegistryKeys.DIMENSION, worldRegistryKey.getValue());
        if (!dimensionOptionsRegistry.contains(dimensionOptionsRegistryKey)) {
            dimensionOptionsRegistry.add(dimensionOptionsRegistryKey, dimensionOptions, RegistryEntryInfo.DEFAULT);
        }
        ((SimpleRegistryMixinInterface<?>) dimensionOptionsRegistry).fuji$setFrozen(original);

        server.worlds.put(world.getRegistryKey(), world);
        ServerWorldEvents.LOAD.invoker().onWorldLoad(server, world);
    }
}
