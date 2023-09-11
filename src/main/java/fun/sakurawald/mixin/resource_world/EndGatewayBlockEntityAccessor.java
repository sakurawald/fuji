package fun.sakurawald.mixin.resource_world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TheEndGatewayBlockEntity.class)
public interface EndGatewayBlockEntityAccessor {

    @Invoker("findOrCreateValidTeleportPos")
    static BlockPos findOrCreateValidTeleportPos(ServerLevel world, BlockPos pos) {
        throw new AssertionError();
    }

    @Invoker("spawnGatewayPortal")
    static void spawnGatewayPortal(ServerLevel world, BlockPos pos, EndGatewayConfiguration config) {
        throw new AssertionError();
    }

    @Invoker("findExitPosition")
    static BlockPos findExitPosition(Level world, BlockPos pos) {
        throw new AssertionError();
    }

    @Invoker("triggerCooldown")
    static void triggerCooldown(Level world, BlockPos pos, BlockState state, TheEndGatewayBlockEntity blockEntity) {
        throw new AssertionError();
    }

    @Accessor
    void setTeleportCooldown(int teleportCooldown);

    @Accessor
    BlockPos getExitPortal();

    @Accessor
    void setExitPortal(BlockPos exitPortalPos);

    @Accessor
    boolean isExactTeleport();
}
