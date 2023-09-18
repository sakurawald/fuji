package fun.sakurawald.module.newbie_welcome;

import com.google.common.base.Stopwatch;
import fun.sakurawald.config.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// Thanks to https://github.com/John-Paul-R/Essential-Commands
@Slf4j
public class RandomTeleport {

    private static final Thread.UncaughtExceptionHandler exceptionHandler = (thread, throwable) -> {
        log.error("Exception in RTP calculator thread", throwable);
    };
    private static final Executor threadExecutor = Executors.newCachedThreadPool(runnable -> {
        var thread = new Thread(runnable, "RTP Location Calculator Thread");
        thread.setUncaughtExceptionHandler(exceptionHandler);
        return thread;
    });

    public static void randomTeleport(ServerPlayer player, ServerLevel world, boolean shouldSetSpawnPoint) {
        threadExecutor.execute(() -> {
            exec(player, world, shouldSetSpawnPoint);
        });
    }

    private static void exec(ServerPlayer player, ServerLevel world, boolean shouldSetSpawnPoint) {
        log.info("Starting RTP location search for {}", player.getGameProfile().getName());
        Stopwatch timer = Stopwatch.createStarted();

        var centerOpt = getRtpCenter(player);
        if (centerOpt.isEmpty()) {
            return;
        }
        Vec3i center = centerOpt.get();

        final var executionContext = new ExecutionContext(world);
        final var heightFinder = HeightFindingStrategy.forWorld(world);

        int timesRun = 0;
        Optional<BlockPos> pos;
        do {
            timesRun++;
            pos = findRtpPosition(world, center, heightFinder, executionContext);
        } while (pos.isEmpty() && timesRun <= ConfigManager.configWrapper.instance().modules.newbie_welcome.random_teleport.max_try_times);

        if (pos.isEmpty()) {
            return;
        }

        // set spawn point
        if (shouldSetSpawnPoint) {
            player.setRespawnPosition(world.dimension(), pos.get(), 0, true, false);
        }

        // teleport the player
        player.teleportTo(world, pos.get().getX() + 0.5, pos.get().getY(), pos.get().getZ() + 0.5, 0, 0);

        var cost = timer.stop();
        log.info("RTP: {} has been teleported to {} {} (cost = {})", player.getGameProfile().getName(), world.dimensionTypeId().location(), pos.get(), cost);
    }

    private static Optional<Vec3i> getRtpCenter(ServerPlayer player) {
        return Optional.of(new Vec3i(0, 0, 0));
    }

    private static Optional<BlockPos> findRtpPosition(ServerLevel world, Vec3i center, HeightFinder heightFinder, ExecutionContext ctx) {
        // Search for a valid y-level (not in a block, underwater, out of the world, etc.)
        final BlockPos targetXZ = getRandomXZ(center);
        final ChunkAccess chunk = world.getChunk(targetXZ);

        for (BlockPos.MutableBlockPos candidateBlock : getChunkCandidateBlocks(chunk.getPos())) {
            final int x = candidateBlock.getX();
            final int z = candidateBlock.getZ();
            final OptionalInt yOpt = heightFinder.getY(chunk, x, z);
            if (yOpt.isEmpty()) {
                continue;
            }
            final int y = yOpt.getAsInt();

            if (isSafePosition(chunk, new BlockPos(x, y - 2, z), ctx)) {
                return Optional.of(new BlockPos(x, y, z));
            }
        }

        // This creates an infinite recursive call in the case where all positions on RTP circle are in water.
        //  Addressed by adding timesRun limit.
        return Optional.empty();
    }

    private static BlockPos getRandomXZ(Vec3i center) {
        // Calculate position on circle perimeter
        var rand = new Random();
        int r_max = ConfigManager.configWrapper.instance().modules.newbie_welcome.random_teleport.max_distance;
        int r_min = ConfigManager.configWrapper.instance().modules.newbie_welcome.random_teleport.min_distance;
        int r = r_max == r_min
                ? r_max
                : rand.nextInt(r_min, r_max);
        final double angle = rand.nextDouble() * 2 * Math.PI;
        final double delta_x = r * Math.cos(angle);
        final double delta_z = r * Math.sin(angle);

        final int new_x = center.getX() + (int) delta_x;
        final int new_z = center.getZ() + (int) delta_z;
        return new BlockPos(new_x, 0, new_z);
    }

    private static boolean isSafePosition(ChunkAccess chunk, BlockPos pos, ExecutionContext ctx) {
        if (pos.getY() <= chunk.getMinBuildHeight()) {
            return false;
        }

        BlockState blockState = chunk.getBlockState(pos);
        return pos.getY() < ctx.topY && blockState.getFluidState().isEmpty() && blockState.getBlock() != Blocks.FIRE;
    }

    public static Iterable<BlockPos.MutableBlockPos> getChunkCandidateBlocks(ChunkPos chunkPos) {
        return () -> new Iterator<>() {
            private final BlockPos.MutableBlockPos _pos = new BlockPos.MutableBlockPos();
            private int _idx = -1;

            @Override
            public boolean hasNext() {
                return _idx < 4;
            }

            @Override
            public BlockPos.MutableBlockPos next() {
                _idx++;
                return switch (_idx) {
                    case 0 -> _pos.set(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());
                    case 1 -> _pos.set(chunkPos.getMinBlockX(), 0, chunkPos.getMaxBlockZ());
                    case 2 -> _pos.set(chunkPos.getMaxBlockX(), 0, chunkPos.getMinBlockZ());
                    case 3 -> _pos.set(chunkPos.getMaxBlockX(), 0, chunkPos.getMaxBlockZ());
                    case 4 -> _pos.set(chunkPos.getMiddleBlockX(), 0, chunkPos.getMiddleBlockZ());
                    default -> throw new IllegalStateException("Unexpected value: " + _idx);
                };
            }
        };
    }

    final static class ExecutionContext {
        public final int topY;
        public final int bottomY;

        public ExecutionContext(ServerLevel world) {
            this.topY = world.getMaxBuildHeight();
            this.bottomY = world.getMinBuildHeight();
        }
    }

}
