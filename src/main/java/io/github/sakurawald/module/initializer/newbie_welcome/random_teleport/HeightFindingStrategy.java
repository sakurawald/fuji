package io.github.sakurawald.module.initializer.newbie_welcome.random_teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

import java.util.OptionalInt;

public enum HeightFindingStrategy implements HeightFinder {
    SKY_TO_SURFACE__FIRST_SOLID(HeightFindingStrategy::findYTopBottom),
    BOTTOM_TO_SKY__FIRST_SAFE_AIR(HeightFindingStrategy::findYBottomUp),
    ;

    private final HeightFinder heightFinder;

    HeightFindingStrategy(HeightFinder heightFinder) {
        this.heightFinder = heightFinder;
    }

    private static int calculateMaxY(ChunkAccess chunk) {
        final int maxY = chunk.getMaxBuildHeight();
        LevelChunkSection[] sections = chunk.getSections();
        int maxSectionIndex = Math.min(sections.length - 1, maxY >> 4);

        for (int index = maxSectionIndex; index >= 0; --index) {
            if (!sections[index].hasOnlyAir()) {
                return Math.min(index << 4 + 15, maxY);
            }
        }

        return Integer.MAX_VALUE;
    }

    public static HeightFindingStrategy forWorld(ServerLevel world) {
        if (world.dimensionTypeId() == BuiltinDimensionTypes.OVERWORLD || world.dimensionTypeId() == BuiltinDimensionTypes.END) {
            return HeightFindingStrategy.SKY_TO_SURFACE__FIRST_SOLID;
        }
        if (world.dimensionTypeId() == BuiltinDimensionTypes.NETHER) {
            return HeightFindingStrategy.BOTTOM_TO_SKY__FIRST_SAFE_AIR;
        }

        // fallback
        return HeightFindingStrategy.SKY_TO_SURFACE__FIRST_SOLID;
    }

    public static OptionalInt findYTopBottom(ChunkAccess chunk, int x, int z) {
        final int maxY = calculateMaxY(chunk);
        final int bottomY = chunk.getMinBuildHeight();
        if (maxY <= bottomY) {
            return OptionalInt.empty();
        }

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(x, maxY, z);
        boolean isAir1 = chunk.getBlockState(mutablePos).isAir(); // Block at head level
        boolean isAir2 = chunk.getBlockState(mutablePos.move(Direction.DOWN)).isAir(); // Block at feet level
        boolean isAir3; // Block below feet

        while (mutablePos.getY() > bottomY) {
            isAir3 = chunk.getBlockState(mutablePos.move(Direction.DOWN)).isAir();
            if (!isAir3 && isAir2 && isAir1) { // If there is a floor block and space for player body+head
                return OptionalInt.of(mutablePos.getY() + 1);
            }

            isAir1 = isAir2;
            isAir2 = isAir3;
        }

        return OptionalInt.empty();
    }

    @SuppressWarnings("deprecation")
    private static OptionalInt findYBottomUp(ChunkAccess chunk, int x, int z) {
        final int topY = getChunkHighestNonEmptySectionYOffsetOrTopY(chunk);
        final int bottomY = chunk.getMinBuildHeight();
        if (topY <= bottomY) {
            return OptionalInt.empty();
        }

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(x, bottomY, z);
        BlockState bsFeet1 = chunk.getBlockState(mutablePos); // Block below feet
        BlockState bsBody2 = chunk.getBlockState(mutablePos.move(Direction.UP)); // Block at feet level
        BlockState bsHead3; // Block at head level

        while (mutablePos.getY() < topY) {
            bsHead3 = chunk.getBlockState(mutablePos.move(Direction.UP));
            if (bsFeet1.isSolid() && bsBody2.isAir() && bsHead3.isAir()) { // If there is a floor block and space for player body+head
                return OptionalInt.of(mutablePos.getY() - 1);
            }

            bsFeet1 = bsBody2;
            bsBody2 = bsHead3;
        }

        return OptionalInt.empty();
    }

    public static int getChunkHighestNonEmptySectionYOffsetOrTopY(ChunkAccess chunk) {
        int i = chunk.getHighestFilledSectionIndex();
        return i == chunk.getMaxBuildHeight() ? chunk.getMinBuildHeight() : SectionPos.sectionToBlockCoord(chunk.getSectionYFromSectionIndex(i));
    }

    @Override
    public OptionalInt getY(ChunkAccess chunk, int x, int z) {
        return heightFinder.getY(chunk, x, z);
    }
}
