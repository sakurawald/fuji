package fun.sakurawald.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndGatewayBlockEntity.class)
public abstract class EndGatewayBlockEntityMixin {

    @Inject(at = @At("HEAD"), method = "tryTeleportingEntity", cancellable = true)
    private static void myTryTeleportingEntity(World world, BlockPos pos, BlockState state, Entity entity, EndGatewayBlockEntity blockEntity, CallbackInfo ci) {
        // cancel the original method invoke and do our own logic
        ci.cancel();

        // do our own logic
        BlockPos blockPos;
        if (!(world instanceof ServerWorld serverWorld) || blockEntity.needsCooldownBeforeTeleporting()) {
            return;
        }
        EndGatewayBlockEntityAccessor blockEntityAccessor = (EndGatewayBlockEntityAccessor) blockEntity;
        blockEntityAccessor.setTeleportCooldown(100);
//        if (blockEntityAccessor.getExitPortalPos() == null && world.getRegistryKey() == World.END) {
        if (blockEntityAccessor.getExitPortalPos() == null) {
            blockPos = EndGatewayBlockEntityAccessor.setupExitPortalLocation(serverWorld, pos);
            blockPos = blockPos.up(10);
            LogUtils.getLogger().debug("Creating portal at {}", blockPos);
            EndGatewayBlockEntityAccessor.createPortal(serverWorld, blockPos, EndGatewayFeatureConfig.createConfig(pos, false));
            blockEntityAccessor.setExitPortalPos(blockPos);
        }
        if (blockEntityAccessor.getExitPortalPos() != null) {
            Entity entity3;
            BlockPos blockPos2 = blockPos = blockEntityAccessor.isExactTeleport() ? ((EndGatewayBlockEntityAccessor) blockEntity).getExitPortalPos() : EndGatewayBlockEntityAccessor.findBestPortalExitPos(world, blockEntityAccessor.getExitPortalPos());
            if (entity instanceof EnderPearlEntity) {
                Entity entity2 = ((EnderPearlEntity) entity).getOwner();
                if (entity2 instanceof ServerPlayerEntity) {
                    Criteria.ENTER_BLOCK.trigger((ServerPlayerEntity) entity2, state);
                }
                if (entity2 != null) {
                    entity3 = entity2;
                    entity.discard();
                } else {
                    entity3 = entity;
                }
            } else {
                entity3 = entity.getRootVehicle();
            }
            entity3.resetPortalCooldown();
            entity3.teleport((double) blockPos.getX() + 0.5, blockPos.getY(), (double) blockPos.getZ() + 0.5);
        }
        EndGatewayBlockEntityAccessor.startTeleportCooldown(world, pos, state, blockEntity);
    }
}
