package io.github.sakurawald.module.initializer.resource_world;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.resource_world.interfaces.SimpleRegistryMixinInterface;
import io.github.sakurawald.common.structure.Position;
import io.github.sakurawald.module.initializer.teleport_warmup.TeleportTicket;
import io.github.sakurawald.module.initializer.teleport_warmup.TeleportWarmupInitializer;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.storage.LevelStorage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ResourceWorldManager {

    private static final TeleportWarmupInitializer TELEPORT_WARMUP_INITIALIZER = ModuleManager.getInitializer(TeleportWarmupInitializer.class);
    private static final Set<ServerWorld> deletionQueue = new ReferenceOpenHashSet<>();

    static {
        ServerTickEvents.START_SERVER_TICK.register(server -> tick());
    }

    public static void enqueueWorldDeletion(ServerWorld world) {
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

    private static boolean tickDeleteWorld(ServerWorld world) {
        if (isWorldUnloaded(world)) {
            delete(world);
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
            if (TELEPORT_WARMUP_INITIALIZER != null) {
                TELEPORT_WARMUP_INITIALIZER.tickets.put(player.getGameProfile().getName(),
                        new TeleportTicket(player
                                , Position.of(player), new Position(overworld, spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5, 0, 0)
                                , true));
            }
            player.teleport(overworld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, overworld.getSpawnAngle(), 0.0F);
        }
    }

    private static boolean isWorldUnloaded(ServerWorld world) {
        return world.getPlayers().isEmpty() && world.getChunkManager().getLoadedChunkCount() <= 0;
    }

    private static SimpleRegistry<DimensionOptions> getDimensionsRegistry(MinecraftServer server) {
        DynamicRegistryManager registryManager = server.getCombinedDynamicRegistries().getCombinedRegistryManager();
        return (SimpleRegistry<DimensionOptions>) registryManager.get(RegistryKeys.DIMENSION);
    }

    private static void delete(ServerWorld world) {
        MinecraftServer server = world.getServer();

        RegistryKey<World> dimensionKey = world.getRegistryKey();
        if (server.worlds.remove(dimensionKey, world)) {
            ServerWorldEvents.UNLOAD.invoker().onWorldUnload(server, world);
            SimpleRegistry<DimensionOptions> dimensionsRegistry = getDimensionsRegistry(server);
            SimpleRegistryMixinInterface.remove(dimensionsRegistry, dimensionKey.getValue());
            LevelStorage.Session session = server.session;
            File worldDirectory = session.getWorldDirectory(dimensionKey).toFile();
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
