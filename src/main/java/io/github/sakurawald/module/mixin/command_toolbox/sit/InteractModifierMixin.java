package io.github.sakurawald.module.mixin.command_toolbox.sit;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.command_toolbox.sit.SitInitializer;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Copyright 2021 BradBot_1
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Mixin(ServerPlayerInteractionManager.class)
public class InteractModifierMixin {

    @Unique
    private static final SitInitializer module = Managers.getModuleManager().getInitializer(SitInitializer.class);

    @Final
    @Shadow
    protected ServerPlayerEntity player;

    @Inject(method = "interactBlock(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
    public void rightClickToSit(@NotNull ServerPlayerEntity player, @NotNull World world, ItemStack stack, Hand hand, @NotNull BlockHitResult hitResult, @NotNull CallbackInfoReturnable<ActionResult> callbackInfoReturnable) {

        /* conditioner */
        var config = Configs.configHandler.model().modules.command_toolbox.sit;

        if (!config.allow_right_click_sit) return;
        if (!config.allow_sneaking_to_sit && player.isSneaking()) return;
        if (player.hasVehicle() || player.isFallFlying() || player.isSleeping() || player.isSwimming() || player.isSpectator())
            return;
        if ((config.require_empty_hand_to_sit && !player.getInventory().getMainHandStack().isEmpty())) return;

        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        if (config.require_no_opaque_block_above_to_sit && world.getBlockState(blockPos.add(0, 1, 0)).isOpaque())
            return;
        if (config.require_stairs_to_sit && (!(block instanceof StairsBlock))) return;

        if (blockState.isSideSolid(world, blockPos, Direction.UP, SideShapeType.RIGID)) return;

        final double maxDist = config.max_distance_to_sit;
        double givenDist = blockPos.getSquaredDistance(player.getBlockPos());
        if (maxDist > 0 && (givenDist > (maxDist * maxDist))) return;

        /* calc offset */
        Vec3d lookTarget;
        if (block instanceof StairsBlock) {
            Direction direction = blockState.get(StairsBlock.FACING);
            Vector3f offset = direction.getUnitVector();
            StairShape stairShape = blockState.get(StairsBlock.SHAPE);
            if (stairShape == StairShape.OUTER_RIGHT || stairShape == StairShape.INNER_RIGHT)
                offset.add(direction.rotateYClockwise().getUnitVector());
            if (stairShape == StairShape.OUTER_LEFT || stairShape == StairShape.INNER_LEFT)
                offset.add(direction.rotateYCounterclockwise().getUnitVector());
            lookTarget = new Vec3d(blockPos.getX() + 0.5 - offset.x(), blockPos.getY(), blockPos.getZ() + 0.5 - offset.z());

        } else {
            lookTarget = player.getPos();
        }

        Entity chair = module.createChair(world, blockPos, new Vec3d(0, -1.7, 0), lookTarget);

        Entity v = player.getVehicle();
        if (v != null) {
            player.setSneaking(true);
            player.tickRiding();
        }
        player.startRiding(chair, true);

        callbackInfoReturnable.setReturnValue(ActionResult.success(true));
    }

    @Inject(method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V", at = @At("HEAD"))
    public void getOutTheChairIfYouAreSpectator(GameMode gameMode, GameMode previousGameMode, CallbackInfo callbackInfo) {
        if (gameMode == GameMode.SPECTATOR && previousGameMode != GameMode.SPECTATOR && player.getVehicle() != null) {
            player.setSneaking(true);
            player.tickRiding();
        }
    }

}
