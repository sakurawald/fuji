package fun.sakurawald.mixin.resource_world;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EndGatewayBlockEntity.class)
public interface EndGatewayBlockEntityAccessor {

    @Invoker("setupExitPortalLocation")
    static BlockPos setupExitPortalLocation(ServerWorld world, BlockPos pos) {
        throw new AssertionError();
    }

    @Invoker("createPortal")
    static void createPortal(ServerWorld world, BlockPos pos, EndGatewayFeatureConfig config) {
        throw new AssertionError();
    }

    @Invoker("findBestPortalExitPos")
    static BlockPos findBestPortalExitPos(World world, BlockPos pos) {
        throw new AssertionError();
    }

    @Invoker("startTeleportCooldown")
    static void startTeleportCooldown(World world, BlockPos pos, BlockState state, EndGatewayBlockEntity blockEntity) {
        throw new AssertionError();
    }

    @Accessor
    void setTeleportCooldown(int teleportCooldown);

    @Accessor
    BlockPos getExitPortalPos();

    @Accessor
    void setExitPortalPos(BlockPos exitPortalPos);

    @Accessor
    boolean isExactTeleport();
}
