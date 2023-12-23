package io.github.sakurawald.module.initializer.resource_world;

import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public class VoidWorldGenerationProgressListener implements ChunkProgressListener {

    public static final VoidWorldGenerationProgressListener INSTANCE = new VoidWorldGenerationProgressListener();

    @Override
    public void updateSpawnPos(ChunkPos spawnPos) {

    }

    @Override
    public void onStatusChange(ChunkPos pos, @Nullable ChunkStatus status) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
