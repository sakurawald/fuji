package io.github.sakurawald.module.initializer.command_toolbox.sit;

import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.EntityHelper;
import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canSit(ServerPlayerEntity player) {
        return !player.hasVehicle() && !player.isFallFlying() && !player.isSleeping() && !player.isSwimming() && !player.isSpectator();
    }

    @CommandNode("sit")
    private int $sit(@CommandSource ServerPlayerEntity player) {
        // fix: if the player stand in the slab/stair block
        BlockPos steppingBlockPos = EntityHelper.getSteppingBlockPos(player);
        BlockState steppingBlockState = player.getWorld().getBlockState(steppingBlockPos);
        if (!canSit(player) || steppingBlockState.isAir() || steppingBlockState.isLiquid()) {
            MessageHelper.sendActionBar(player, "sit.fail");
            return CommandHelper.Return.FAIL;
        }

        Vec3d lookTarget = player.getPos().add(0.5, 0, 0.5);
        Entity entity = makeChairEntity(player.getWorld(), steppingBlockPos, lookTarget);
        CHAIR_ENTITY_LIST.add(entity);
        player.startRiding(entity, true);

        return CommandHelper.Return.SUCCESS;
    }

    public @NotNull Entity makeChairEntity(@NotNull World world, @NotNull BlockPos targetBlockPos, @Nullable Vec3d target) {

        Vec3d chairEntityPosition = targetBlockPos.toBottomCenterPos().add(0, 0.5, 0).add(SitInitializer.CHAIR_ENTITY_OFFSET);
        BlockState targetBlockStage = world.getBlockState(targetBlockPos);

        // if there is a slab/stair block under the player, then we should not sit on the ground.
        VoxelShape outlineShape = targetBlockStage.getOutlineShape(world, targetBlockPos);
        if (!Block.isFaceFullSquare(outlineShape, Direction.UP)) {
            double averageLengthY = outlineShape.getBoundingBoxes().stream().mapToDouble(Box::getLengthY).average().orElse(0);
            chairEntityPosition = chairEntityPosition.add(0, -(1 - averageLengthY), 0);
        }

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
                return targetBlockPos;
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

                // sync the leg position
                Entity passenger = getFirstPassenger();
                if (passenger != null) {
                    this.setYaw(passenger.getYaw());
                    this.setPitch(passenger.getPitch());
                }

                // call super
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
