package fun.sakurawald.mixin.resource_world;

import com.mojang.logging.LogUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TheEndGatewayBlockEntity.class)
public abstract class EndGatewayBlockEntityMixin {

    @Inject(at = @At("HEAD"), method = "teleportEntity", cancellable = true)
    private static void myTeleportEntity(Level world, BlockPos pos, BlockState state, Entity entity, TheEndGatewayBlockEntity blockEntity, CallbackInfo ci) {
        // cancel the original method invoke and do our own logic
        ci.cancel();

        // do our own logic
        BlockPos blockPos;
        if (!(world instanceof ServerLevel serverWorld) || blockEntity.isCoolingDown()) {
            return;
        }
        EndGatewayBlockEntityAccessor blockEntityAccessor = (EndGatewayBlockEntityAccessor) blockEntity;
        blockEntityAccessor.setTeleportCooldown(100);
//        if (blockEntityAccessor.getExitPortalPos() == null && world.getRegistryKey() == World.END) {
        if (blockEntityAccessor.getExitPortal() == null) {
            blockPos = EndGatewayBlockEntityAccessor.findOrCreateValidTeleportPos(serverWorld, pos);
            blockPos = blockPos.above(10);
            LogUtils.getLogger().debug("Creating portal at {}", blockPos);
            EndGatewayBlockEntityAccessor.spawnGatewayPortal(serverWorld, blockPos, EndGatewayConfiguration.knownExit(pos, false));
            blockEntityAccessor.setExitPortal(blockPos);
        }
        if (blockEntityAccessor.getExitPortal() != null) {
            Entity entity3;
            BlockPos blockPos2 = blockPos = blockEntityAccessor.isExactTeleport() ? ((EndGatewayBlockEntityAccessor) blockEntity).getExitPortal() : EndGatewayBlockEntityAccessor.findExitPosition(world, blockEntityAccessor.getExitPortal());
            if (entity instanceof ThrownEnderpearl) {
                Entity entity2 = ((ThrownEnderpearl) entity).getOwner();
                if (entity2 instanceof ServerPlayer) {
                    CriteriaTriggers.ENTER_BLOCK.trigger((ServerPlayer) entity2, state);
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
            entity3.setPortalCooldown();
            entity3.teleportToWithTicket((double) blockPos.getX() + 0.5, blockPos.getY(), (double) blockPos.getZ() + 0.5);
        }
        EndGatewayBlockEntityAccessor.triggerCooldown(world, pos, state, blockEntity);
    }
}
