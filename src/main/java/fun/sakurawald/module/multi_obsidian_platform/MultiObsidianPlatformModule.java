package fun.sakurawald.module.multi_obsidian_platform;

import fun.sakurawald.ServerMain;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

@Slf4j
public class MultiObsidianPlatformModule {

    private static BlockPos findCenterEndPortalBlock(BlockPos bp) {
        ServerLevel overworld = ServerMain.SERVER.overworld();
        if (overworld.getBlockState(bp.north()) != Blocks.END_PORTAL.defaultBlockState() && overworld.getBlockState(bp.west()) != Blocks.END_PORTAL.defaultBlockState()) {
            return bp.south().east();
        }
        if (overworld.getBlockState(bp.north()) != Blocks.END_PORTAL.defaultBlockState() && overworld.getBlockState(bp.east()) != Blocks.END_PORTAL.defaultBlockState()) {
            return bp.south().west();
        }
        if (overworld.getBlockState(bp.south()) != Blocks.END_PORTAL.defaultBlockState() && overworld.getBlockState(bp.west()) != Blocks.END_PORTAL.defaultBlockState()) {
            return bp.north().east();
        }
        if (overworld.getBlockState(bp.south()) != Blocks.END_PORTAL.defaultBlockState() && overworld.getBlockState(bp.east()) != Blocks.END_PORTAL.defaultBlockState()) {
            return bp.north().west();
        }
        if (overworld.getBlockState(bp.north()) != Blocks.END_PORTAL.defaultBlockState()) {
            return bp.south();
        }
        if (overworld.getBlockState(bp.south()) != Blocks.END_PORTAL.defaultBlockState()) {
            return bp.north();
        }
        if (overworld.getBlockState(bp.west()) != Blocks.END_PORTAL.defaultBlockState()) {
            return bp.east();
        }
        if (overworld.getBlockState(bp.east()) != Blocks.END_PORTAL.defaultBlockState()) {
            return bp.west();
        }
        return bp;
    }

    public static BlockPos transform(BlockPos bp) {
        bp = findCenterEndPortalBlock(bp);
        int factor = 5;
        return new BlockPos(bp.getX() / factor, 50, bp.getZ() / factor);
    }

    public static void makeObsidianPlatform(ServerLevel serverLevel, BlockPos centerBlockPos) {
        int i = centerBlockPos.getX();
        int j = centerBlockPos.getY() - 2;
        int k = centerBlockPos.getZ();
        BlockPos.betweenClosed(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach(blockPos -> serverLevel.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState()));
        BlockPos.betweenClosed(i - 2, j, k - 2, i + 2, j, k + 2).forEach(blockPos -> serverLevel.setBlockAndUpdate(blockPos, Blocks.OBSIDIAN.defaultBlockState()));
    }

}
