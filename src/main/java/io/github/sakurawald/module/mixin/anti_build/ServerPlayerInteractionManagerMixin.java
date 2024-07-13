package io.github.sakurawald.module.mixin.anti_build;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.util.IdentifierUtil;
import io.github.sakurawald.util.MessageUtil;
import io.github.sakurawald.util.PermissionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Shadow
    protected ServerWorld world;

    @Shadow
    @Final
    protected ServerPlayerEntity player;

    @Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
    void $tryBreak(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = this.world.getBlockState(blockPos);

        String id = IdentifierUtil.getBlockStateIdentifier(blockState);
        if (Configs.configHandler.model().modules.anti_build.anti.break_block.id.contains(id)
                && !PermissionUtil.hasPermission(player, "fuji.anti_build.%s.bypass.%s".formatted("break_block", id))
        ) {
            MessageUtil.sendMessage(player, "anti_build.disallow");
            cir.setReturnValue(false);
        }

    }

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    void $interactItem(ServerPlayerEntity serverPlayerEntity, World world, ItemStack itemStack, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        String id = IdentifierUtil.getItemStackIdentifier(itemStack);

        if (Configs.configHandler.model().modules.anti_build.anti.interact_item.id.contains(id)
                && !PermissionUtil.hasPermission(player, "fuji.anti_build.%s.bypass.%s".formatted("interact_item", id))
        ) {
            MessageUtil.sendMessage(player, "anti_build.disallow");
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    void $interactBlock(ServerPlayerEntity serverPlayerEntity, World world, ItemStack itemStack, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> cir) {
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        String id = IdentifierUtil.getBlockStateIdentifier(blockState);

        if (Configs.configHandler.model().modules.anti_build.anti.interact_block.id.contains(id)
                && !PermissionUtil.hasPermission(player, "fuji.anti_build.%s.bypass.%s".formatted("interact_block", id))
        ) {
            MessageUtil.sendMessage(player, "anti_build.disallow");
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

}
