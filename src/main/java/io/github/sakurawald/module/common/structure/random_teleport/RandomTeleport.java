package io.github.sakurawald.module.common.structure.random_teleport;

import com.google.common.base.Stopwatch;
import io.github.sakurawald.module.common.structure.Position;
import io.github.sakurawald.module.common.structure.TeleportSetup;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.IdentifierHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Consumer;

// Thanks to https://github.com/John-Paul-R/Essential-Commands
public class RandomTeleport {

    public static void request(ServerPlayerEntity player, TeleportSetup setup, Consumer<Position> postConsumer) {
        LogUtil.info("Starting RTP location search for {}", player.getGameProfile().getName());
        Stopwatch timer = Stopwatch.createStarted();

        ServerWorld world = IdentifierHelper.ofServerWorld(Identifier.of(setup.getDimension()));

        final HeightFindingStrategy heightFinder = HeightFindingStrategy.forWorld(world);

        Optional<BlockPos> result;

        int tryiedTimes = 0;
        do {
            tryiedTimes++;
            result = findRtpPosition(setup, heightFinder);
        } while (result.isEmpty() && tryiedTimes <= setup.getMaxTryTimes());

        if (result.isEmpty()) {
            return;
        }

        // teleport the player
        Position position = new Position(world, result.get().getX() + 0.5, result.get().getY(), result.get().getZ() + 0.5, 0, 0);
        position.teleport(player);

        // post consumer
        if (postConsumer != null) {
            postConsumer.accept(position);
        }

        // cost
        var cost = timer.stop();
        LogUtil.info("RTP: {} has been teleported to ({} {} {} {}) (cost = {})", player.getGameProfile().getName(), world.getRegistryKey().getValue(), result.get().getX(), result.get().getY(), result.get().getZ(), cost);
    }

    private static Optional<BlockPos> findRtpPosition(TeleportSetup setup, HeightFinder heightFinder) {
        // Search for a valid y-level (not in a block, underwater, out of the world, etc.)
        final BlockPos targetXZ = getRandomXZ(setup);
        final Chunk chunk = setup.ofWorld().getChunk(targetXZ);

        for (BlockPos.Mutable candidateBlock : getChunkCandidateBlocks(chunk.getPos())) {
            final int x = candidateBlock.getX();
            final int z = candidateBlock.getZ();
            final OptionalInt yOpt = heightFinder.getY(chunk, x, z);
            if (yOpt.isEmpty()) {
                continue;
            }
            final int y = yOpt.getAsInt();

            if (isSafePosition(setup, chunk, new BlockPos(x, y - 2, z))) {
                return Optional.of(new BlockPos(x, y, z));
            }
        }

        return Optional.empty();
    }

    private static BlockPos getRandomXZ(TeleportSetup setup) {
        var rand = new Random();

        int r_min = setup.getMinRange();
        int r_max = setup.getMaxRange();
        int r = r_max == r_min
                ? r_max
                : rand.nextInt(r_min, r_max);
        final double angle = rand.nextDouble() * 2 * Math.PI;
        final double delta_x = r * Math.cos(angle);
        final double delta_z = r * Math.sin(angle);

        int new_x = setup.getCenterX() + (int) delta_x;
        int new_z = setup.getCenterZ() + (int) delta_z;
        return new BlockPos(new_x, 0, new_z);
    }

    private static boolean isSafePosition(TeleportSetup setup, Chunk chunk, BlockPos pos) {
        if (pos.getY() <= chunk.getBottomY()) {
            return false;
        }

        BlockState blockState = chunk.getBlockState(pos);
        int worldTopY = setup.ofWorld().getTopY();
        int worldBottomY = setup.ofWorld().getBottomY();
        return pos.getY() < worldTopY
                && pos.getY() > worldBottomY
                && blockState.getFluidState().isEmpty()
                && blockState.getBlock() != Blocks.POWDER_SNOW
                && blockState.getBlock() != Blocks.FIRE;
    }

    public static Iterable<BlockPos.Mutable> getChunkCandidateBlocks(ChunkPos chunkPos) {
        return () -> new Iterator<>() {
            private final BlockPos.Mutable bp = new BlockPos.Mutable();
            private int i = -1;

            @Override
            public boolean hasNext() {
                return i < 4;
            }

            @Override
            public BlockPos.Mutable next() {
                i++;
                return switch (i) {
                    case 0 -> bp.set(chunkPos.getStartX(), 0, chunkPos.getStartZ());
                    case 1 -> bp.set(chunkPos.getStartX(), 0, chunkPos.getEndZ());
                    case 2 -> bp.set(chunkPos.getEndX(), 0, chunkPos.getStartZ());
                    case 3 -> bp.set(chunkPos.getEndX(), 0, chunkPos.getEndZ());
                    case 4 -> bp.set(chunkPos.getCenterX(), 0, chunkPos.getCenterZ());
                    default -> throw new IllegalStateException("Unexpected value: " + i);
                };
            }
        };
    }

}
