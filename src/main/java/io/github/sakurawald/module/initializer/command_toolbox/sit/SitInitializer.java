package io.github.sakurawald.module.initializer.command_toolbox.sit;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.EntityHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class SitInitializer extends ModuleInitializer {

    public static final Vec3d CHAIR_ENTITY_OFFSET = new Vec3d(0, -1.375, 0);
    private static final Set<Entity> CHAIR_ENTITY_LIST = new HashSet<>();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> CHAIR_ENTITY_LIST.forEach(e -> {
            if (e.isAlive()) e.kill();
        }));
    }

    public boolean canSit(ServerPlayerEntity player) {
        return !player.hasVehicle() && !player.isFallFlying() && !player.isSleeping() && !player.isSwimming() && !player.isSpectator();
    }

    @Command("sit")
    private int $sit(@CommandSource ServerPlayerEntity player) {
        // fix: if the player stand in the slab/stair block
        BlockPos blockPosBelowPlayer = EntityHelper.getBlockPosBelowEntity(player);
        BlockState blockBelowPlayer = player.getWorld().getBlockState(blockPosBelowPlayer);
        if (!canSit(player) || blockBelowPlayer.isAir() || blockBelowPlayer.isLiquid()) {
            MessageHelper.sendActionBar(player, "sit.fail");
            return CommandHelper.Return.FAIL;
        }

        Vec3d chairEntityPosition = blockPosBelowPlayer.toBottomCenterPos().add(0, 0.5, 0).add(SitInitializer.CHAIR_ENTITY_OFFSET);

        // if there is a slab/stair block under the player, then we should not sit on the ground.
        if (blockBelowPlayer.getBlock() instanceof StairsBlock
        || blockBelowPlayer.getBlock() instanceof SlabBlock) {
            chairEntityPosition = chairEntityPosition.add(0, -0.5, 0);
        }

        Entity entity = makeChairEntity(player.getWorld(), chairEntityPosition, blockPosBelowPlayer, player.getPos().add(0.5,0,0.5));
        CHAIR_ENTITY_LIST.add(entity);
        player.startRiding(entity, true);

        return CommandHelper.Return.SUCCESS;
    }

    public @NotNull Entity makeChairEntity(@NotNull World world, @NotNull Vec3d chairEntityPosition, @NotNull BlockPos boundBlockPosition, @Nullable Vec3d target) {

        ArmorStandEntity entity = new ArmorStandEntity(world, chairEntityPosition.x, chairEntityPosition.y, chairEntityPosition.z) {

            private boolean hasPassenger = false;

            @Override
            protected void addPassenger(Entity passenger) {
                super.addPassenger(passenger);
                hasPassenger = true;
            }

            @Override
            public boolean canMoveVoluntarily() {
                return false;
            }

            @Override
            public boolean collidesWithStateAtPos(BlockPos blockPos, BlockState blockState) {
                return false;
            }

            public BlockPos getChairBlockPos() {
                return boundBlockPosition;
            }

            public boolean isChairBlockBroken() {
                return getEntityWorld().getBlockState(getChairBlockPos()).isAir();
            }

            // note: if the chair block is broken, the method `updatePassengerForDismount` will not be called.
            @Override
            public Vec3d updatePassengerForDismount(LivingEntity livingEntity) {
                return getChairBlockPos().toCenterPos().add(0, 1, 0);
            }

            @Override
            protected void removePassenger(Entity entity) {
                // call super to remove the passenger
                super.removePassenger(entity);

                // if the chair block is break
                if (isChairBlockBroken()) {
                    entity.refreshPositionAndAngles(getChairBlockPos().add(0, 1, 0), entity.getYaw(), entity.getPitch());
                }
            }

            @Override
            public void tick() {
                if (hasPassenger && getPassengerList().isEmpty()) {
                    kill();
                }

                if (isChairBlockBroken()) {
                    kill();
                }

                super.tick();
            }

        };

        // chair entity props
        if (target != null) {
            entity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.subtract(0, (target.getY() * 2), 0));
        }
        entity.setInvisible(true);
        entity.setInvulnerable(true);
        entity.setCustomName(Text.literal("FUJI-SIT"));
        entity.setNoGravity(true);

        world.spawnEntity(entity);
        return entity;
    }
}
