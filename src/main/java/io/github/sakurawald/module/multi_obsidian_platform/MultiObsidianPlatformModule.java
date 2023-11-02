package io.github.sakurawald.module.multi_obsidian_platform;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.base.ConfigManager;
import io.github.sakurawald.module.AbstractModule;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;


public class MultiObsidianPlatformModule extends AbstractModule {

    private final HashMap<BlockPos, BlockPos> TRANSFORM_CACHE = new HashMap<>();

    /* this method is used to fix Entity#position() async */
    private BlockPos findNearbyEndPortalBlock(BlockPos bp) {
        ServerLevel overworld = Fuji.SERVER.overworld();

        // should we find nearby END_PORTAL block ?
        if (overworld.getBlockState(bp) == Blocks.END_PORTAL.defaultBlockState()) return bp;

        // let's find nearby END_PORTAL block
        int radius = 3;
        for (int y = -radius; y < radius; y++) {
            for (int x = -radius; x < radius; x++) {
                for (int z = -radius; z < radius; z++) {
                    BlockPos test = bp.offset(x, y, z);
                    if (overworld.getBlockState(test) == Blocks.END_PORTAL.defaultBlockState()) return test;
                }
            }
        }

        Fuji.log.warn("BlockPos {} is not END_PORTAL and we can't find a nearby END_PORTAL block !", bp);
        return bp;
    }

    private BlockPos findCenterEndPortalBlock(BlockPos bp) {
        ServerLevel overworld = Fuji.SERVER.overworld();
        if (overworld.getBlockState(bp.north()) != Blocks.END_PORTAL.defaultBlockState()) {
            if (overworld.getBlockState(bp.west()) != Blocks.END_PORTAL.defaultBlockState()) {
                return bp.south().east();
            } else if (overworld.getBlockState(bp.east()) != Blocks.END_PORTAL.defaultBlockState()) {
                return bp.south().west();
            }
            return bp.south();
        }
        if (overworld.getBlockState(bp.south()) != Blocks.END_PORTAL.defaultBlockState()) {
            if (overworld.getBlockState(bp.west()) != Blocks.END_PORTAL.defaultBlockState()) {
                return bp.north().east();
            } else if (overworld.getBlockState(bp.east()) != Blocks.END_PORTAL.defaultBlockState()) {
                return bp.north().west();
            }
            return bp.north();
        }
        if (overworld.getBlockState(bp.west()) != Blocks.END_PORTAL.defaultBlockState()) {
            return bp.east();
        }
        if (overworld.getBlockState(bp.east()) != Blocks.END_PORTAL.defaultBlockState()) {
            return bp.west();
        }
        // This is the center block.
        return bp;
    }

    public BlockPos transform(BlockPos bp) {
        if (TRANSFORM_CACHE.containsKey(bp)) {
            return TRANSFORM_CACHE.get(bp);
        }
        // fix: for sand-dupe, the blockpos (x, ?, z) of sand may differ +1 or -1
        bp = findNearbyEndPortalBlock(bp);
        bp = findCenterEndPortalBlock(bp);
        double factor = ConfigManager.configWrapper.instance().modules.multi_obsidian_platform.factor;
        int x = (int) (bp.getX() / factor);
        int y = 50;
        int z = (int) (bp.getZ() / factor);
        int x_offset = x % 16;
        int z_offset = z % 16;
        x -= x_offset;
        z -= z_offset;
        x += 100;
        TRANSFORM_CACHE.put(bp, new BlockPos(x, y, z));
        return TRANSFORM_CACHE.get(bp);
    }

    public void makeObsidianPlatform(ServerLevel serverLevel, BlockPos centerBlockPos) {
        int i = centerBlockPos.getX();
        int j = centerBlockPos.getY() - 2;
        int k = centerBlockPos.getZ();
        BlockPos.betweenClosed(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach(blockPos -> serverLevel.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState()));
        BlockPos.betweenClosed(i - 2, j, k - 2, i + 2, j, k + 2).forEach(blockPos -> serverLevel.setBlockAndUpdate(blockPos, Blocks.OBSIDIAN.defaultBlockState()));
    }


}
