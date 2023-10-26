package io.github.sakurawald.module.resource_world;

import io.github.sakurawald.mixin.resource_world.MinecraftServerAccessor;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.resource_world.interfaces.SimpleRegistryMixinInterface;
import io.github.sakurawald.module.teleport_warmup.Position;
import io.github.sakurawald.module.teleport_warmup.TeleportTicket;
import io.github.sakurawald.module.teleport_warmup.TeleportWarmupModule;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ResourceWorldManager {

    private static final TeleportWarmupModule teleportWarmupModule = ModuleManager.getOrNewInstance(TeleportWarmupModule.class);
    private static final Set<ServerLevel> deletionQueue = new ReferenceOpenHashSet<>();

    static {
        ServerTickEvents.START_SERVER_TICK.register(server -> tick());
    }

    public static void enqueueWorldDeletion(ServerLevel world) {
        MinecraftServer server = world.getServer();
        server.submit(() -> {
            deletionQueue.add(world);
        });
    }

    private static void tick() {
        if (!deletionQueue.isEmpty()) {
            deletionQueue.removeIf(ResourceWorldManager::tickDeleteWorld);
        }
    }

    private static boolean tickDeleteWorld(ServerLevel world) {
        if (isWorldUnloaded(world)) {
            delete(world);
            return true;
        } else {
            kickPlayers(world);
            return false;
        }
    }

    private static void kickPlayers(ServerLevel world) {
        if (world.players().isEmpty()) {
            return;
        }

        ServerLevel overworld = world.getServer().overworld();
        BlockPos spawnPos = overworld.getSharedSpawnPos();

        List<ServerPlayer> players = new ArrayList<>(world.players());
        for (ServerPlayer player : players) {
            // fix: if the player is inside resource-world while resetting the worlds, then resource worlds will delay its deletion until the player left the resource-world.
            if (teleportWarmupModule != null) {
                teleportWarmupModule.tickets.put(player.getGameProfile().getName(),
                        new TeleportTicket(player
                                , Position.of(player), new Position(overworld, spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5, 0, 0)
                                , true));
            }
            player.teleportTo(overworld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, overworld.getSharedSpawnAngle(), 0.0F);
        }
    }

    private static boolean isWorldUnloaded(ServerLevel world) {
        return world.players().isEmpty() && world.getChunkSource().getLoadedChunksCount() <= 0;
    }

    private static MappedRegistry<LevelStem> getDimensionsRegistry(MinecraftServer server) {
        RegistryAccess registryManager = server.registries().compositeAccess();
        return (MappedRegistry<LevelStem>) registryManager.registryOrThrow(Registries.LEVEL_STEM);
    }

    private static void delete(ServerLevel world) {
        MinecraftServer server = world.getServer();
        MinecraftServerAccessor serverAccess = (MinecraftServerAccessor) server;

        ResourceKey<Level> dimensionKey = world.dimension();
        if (serverAccess.getLevels().remove(dimensionKey, world)) {
            ServerWorldEvents.UNLOAD.invoker().onWorldUnload(server, world);
            MappedRegistry<LevelStem> dimensionsRegistry = getDimensionsRegistry(server);
            SimpleRegistryMixinInterface.remove(dimensionsRegistry, dimensionKey.location());
            LevelStorageSource.LevelStorageAccess session = serverAccess.getStorageSource();
            File worldDirectory = session.getDimensionPath(dimensionKey).toFile();
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

}
