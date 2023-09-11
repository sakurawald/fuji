package fun.sakurawald.module.resource_world;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Executor;

public class ResourceWorld extends ServerLevel {

    public ResourceWorld(MinecraftServer server, Executor workerExecutor, LevelStorageSource.LevelStorageAccess session, ServerLevelData properties, ResourceKey<Level> worldKey, LevelStem dimensionOptions, ChunkProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<CustomSpawner> spawners, boolean shouldTickTime, @Nullable RandomSequences randomSequencesState) {
        super(server, workerExecutor, session, properties, worldKey, dimensionOptions, worldGenerationProgressListener, debugWorld, seed, spawners, shouldTickTime, randomSequencesState);
    }

    /*
        The main issue is that the runtime world must return the custom seed through World#getSeed, including within the ServerWorld constructor.
        The solution is to override World#getSeed in a way that the seed is initialized before it is called.
        Please note that: all the resource world will not save its data (properties) into level.dat, so if you restart the server.
        then the seed of resource world will be changed randomly (and then the chunk generator will generate new chunks with the new seed).
     */
    @Override
    public long getSeed() {
        return ((ResourceWorldProperties) this.levelData).getSeed();
    }
}
