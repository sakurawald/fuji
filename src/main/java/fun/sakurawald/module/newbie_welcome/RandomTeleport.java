package fun.sakurawald.module.newbie_welcome;

import com.google.common.base.Stopwatch;
import fun.sakurawald.ModMain;
import fun.sakurawald.config.ConfigManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;

import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// Thanks to https://github.com/John-Paul-R/Essential-Commands
public class RandomTeleport {

    private static final Thread.UncaughtExceptionHandler exceptionHandler = (thread, throwable) -> {
        ModMain.LOGGER.error("Exception in RTP calculator thread", throwable);
    };
    private static final Executor threadExecutor = Executors.newCachedThreadPool(runnable -> {
        var thread = new Thread(runnable, "RTP Location Calculator Thread");
        thread.setUncaughtExceptionHandler(exceptionHandler);
        return thread;
    });

    public static void randomTeleport(ServerPlayerEntity player) {
        threadExecutor.execute(() -> {
            ModMain.LOGGER.info(
                    String.format(
                            "Starting RTP location search for %s",
                            player.getGameProfile().getName()
                    ));

            Stopwatch timer = Stopwatch.createStarted();

            exec(player, player.getServerWorld());

            var totalTime = timer.stop();
            ModMain.LOGGER.info(
                    String.format(
                            "Total RTP Time: %s",
                            totalTime
                    ));

            player.setSpawnPoint(player.getServerWorld().getRegistryKey(), player.getBlockPos(), 0, true, false);
        });

    }

    private static void exec(ServerPlayerEntity player, ServerWorld world) {
        var centerOpt = getRtpCenter(player);
        if (centerOpt.isEmpty()) {
            return;
        }
        Vec3i center = centerOpt.get();

        final var executionContext = new ExecutionContext(world);
        final var heightFinder = HeightFindingStrategy.forWorld(world.getRegistryKey());

        int timesRun = 0;
        Optional<BlockPos> pos;
        do {
            timesRun++;
            pos = findRtpPosition(world, center, heightFinder, executionContext);
        } while (pos.isEmpty() && timesRun <= ConfigManager.configWrapper.instance().modules.newbie_welcome.random_teleport.max_try_times);

        if (pos.isEmpty()) {
            return;
        }

        // Teleport the player
        player.teleport(world, pos.get().getX(), pos.get().getY(), pos.get().getZ(), 0, 0);
    }

    private static Optional<Vec3i> getRtpCenter(ServerPlayerEntity player) {
        return Optional.of(new Vec3i(0, 0, 0));
    }

    private static Optional<BlockPos> findRtpPosition(ServerWorld world, Vec3i center, HeightFinder heightFinder, ExecutionContext ctx) {
        // Search for a valid y-level (not in a block, underwater, out of the world, etc.)
        final BlockPos targetXZ = getRandomXZ(center);
        final Chunk chunk = world.getChunk(targetXZ);

        for (BlockPos.Mutable candidateBlock : getChunkCandidateBlocks(chunk.getPos())) {
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

    private static boolean isSafePosition(Chunk chunk, BlockPos pos, ExecutionContext ctx) {
        if (pos.getY() <= chunk.getBottomY()) {
            return false;
        }

        BlockState blockState = chunk.getBlockState(pos);
        return pos.getY() < ctx.topY && blockState.getFluidState().isEmpty() && blockState.getBlock() != Blocks.FIRE;
    }

    public static Iterable<BlockPos.Mutable> getChunkCandidateBlocks(ChunkPos chunkPos) {
        return () -> new Iterator<>() {
            private final BlockPos.Mutable _pos = new BlockPos.Mutable();
            private int _idx = -1;

            @Override
            public boolean hasNext() {
                return _idx < 4;
            }

            @Override
            public BlockPos.Mutable next() {
                _idx++;
                return switch (_idx) {
                    case 0 -> _pos.set(chunkPos.getStartX(), 0, chunkPos.getStartZ());
                    case 1 -> _pos.set(chunkPos.getStartX(), 0, chunkPos.getEndZ());
                    case 2 -> _pos.set(chunkPos.getEndX(), 0, chunkPos.getStartZ());
                    case 3 -> _pos.set(chunkPos.getEndX(), 0, chunkPos.getEndZ());
                    case 4 -> _pos.set(chunkPos.getCenterX(), 0, chunkPos.getCenterZ());
                    default -> throw new IllegalStateException("Unexpected value: " + _idx);
                };
            }
        };
    }

    final static class ExecutionContext {
        public final int topY;
        public final int bottomY;

        public ExecutionContext(ServerWorld world) {
            this.topY = world.getTopY();
            this.bottomY = world.getBottomY();
        }
    }

}
