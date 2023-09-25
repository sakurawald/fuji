package fun.sakurawald.module.multi_obsidian_platform;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

@Slf4j

public class MultiObsidianPlatformModule {

    public static BlockPos transform(BlockPos bp) {
        int xDeviation = bp.getX() > 0 ? 2 : -2;
        int yDeviation = bp.getY() > 0 ? 2 : -2;
        int factor = 5;
        return new BlockPos((bp.getX() + xDeviation) / factor, 50, (bp.getZ() + yDeviation) / factor);
    }

    public static void makeObsidianPlatform(ServerLevel serverLevel, BlockPos centerBlockPos) {
        int i = centerBlockPos.getX();
        int j = centerBlockPos.getY() - 2;
        int k = centerBlockPos.getZ();
        BlockPos.betweenClosed(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach(blockPos -> serverLevel.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState()));
        BlockPos.betweenClosed(i - 2, j, k - 2, i + 2, j, k + 2).forEach(blockPos -> serverLevel.setBlockAndUpdate(blockPos, Blocks.OBSIDIAN.defaultBlockState()));
    }

}
