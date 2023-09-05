package fun.sakurawald.module.resource_world;

import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public class VoidWorldGenerationProgressListener implements WorldGenerationProgressListener {

    public static final VoidWorldGenerationProgressListener INSTANCE = new VoidWorldGenerationProgressListener();

    @Override
    public void start(ChunkPos spawnPos) {

    }

    @Override
    public void setChunkStatus(ChunkPos pos, @Nullable ChunkStatus status) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
