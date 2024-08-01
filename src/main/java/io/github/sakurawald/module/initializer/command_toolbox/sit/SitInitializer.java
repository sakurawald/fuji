package io.github.sakurawald.module.initializer.command_toolbox.sit;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
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

    public final Set<Entity> CHAIRS = new HashSet<>();


    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> CHAIRS.forEach(e -> {
            if (e.isAlive()) e.kill();
        }));
    }

    @Command("sit")
    private int $sit(@CommandSource ServerPlayerEntity player) {
        BlockState blockState = player.getWorld().getBlockState(new BlockPos(player.getBlockX(), player.getBlockY() - 1, player.getBlockZ()));

        if (player.hasVehicle() || player.isFallFlying() || player.isSleeping() || player.isSwimming() || player.isSpectator() || blockState.isAir() || blockState.isLiquid()) {
            MessageHelper.sendActionBar(player, "sit.fail");
            return CommandHelper.Return.FAIL;
        }

        Vec3d blockPosOffset = new Vec3d(0, -1.7, 0);

        // fix: issue /sit on a chair
        BlockState blocksBelow = player.getWorld().getBlockState(player.getBlockPos().add(0, -1, 0));
        if (blocksBelow.getBlock() instanceof StairsBlock) {
            blockPosOffset = blockPosOffset.add(new Vec3d(0, -1, 0));
        }

        Entity entity = createChair(player.getWorld(), player.getBlockPos(), blockPosOffset, player.getPos());
        CHAIRS.add(entity);
        player.startRiding(entity, true);

        return CommandHelper.Return.SUCCESS;
    }

    public @NotNull Entity createChair(@NotNull World world, @NotNull BlockPos blockPos, @NotNull Vec3d blockPosOffset, @Nullable Vec3d target) {

        // make chair entity
        ArmorStandEntity entity = new ArmorStandEntity(world, 0.5d + blockPos.getX() + blockPosOffset.getX(), blockPos.getY() + blockPosOffset.getY(), 0.5d + blockPos.getZ() + blockPosOffset.getZ()) {

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

            @Override
            protected void removePassenger(Entity entity) {
                super.removePassenger(entity);

                LogUtil.warn("remove passenger = {}", entity);

                // if the block below chair block is air
                // NOTE: this seems no work, the player still falling because of the gravity
                if (getEntityWorld().getBlockState(getBlockPos().add(0, 1, 0)).isAir()) {
                    BlockPos readjust = getBlockPos().add(0, 3, 0);
                    entity.refreshPositionAndAngles(readjust, entity.getYaw(), entity.getPitch());
                    return;
                }

                // if the chair block is break
                if (getEntityWorld().getBlockState(getBlockPos().add(0, 2, 0)).isAir()) {
                    BlockPos readjust = getBlockPos().add(0, 2, 0);
                    entity.refreshPositionAndAngles(readjust, entity.getYaw(), entity.getPitch());
                    return;
                }
            }

            @Override
            public void tick() {
                if (hasPassenger && getPassengerList().isEmpty()) {
                    kill();
                }

                // check if the chair block is broken -> the chair entity is 2 blocks below the player
                if (getEntityWorld().getBlockState(getBlockPos().add(0, 2, 0)).isAir()) {
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
