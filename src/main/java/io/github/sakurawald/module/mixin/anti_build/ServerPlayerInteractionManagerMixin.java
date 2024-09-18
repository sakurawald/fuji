package io.github.sakurawald.module.mixin.anti_build;

import io.github.sakurawald.core.auxiliary.minecraft.RegistryHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.PermissionHelper;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.anti_build.AntiBuildInitializer;
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
import org.jetbrains.annotations.NotNull;
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
    void $tryBreak(BlockPos blockPos, @NotNull CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = this.world.getBlockState(blockPos);

        String id = RegistryHelper.ofString(blockState);
        if (Managers.getModuleManager().getInitializer(AntiBuildInitializer.class).config.getModel().anti.break_block.id.contains(id)
                && !PermissionHelper.hasPermission(player.getUuid(), "fuji.anti_build.%s.bypass.%s".formatted("break_block", id))
        ) {
            LocaleHelper.sendMessageByKey(player, "anti_build.disallow");
            cir.setReturnValue(false);
        }

    }

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    void $interactItem(ServerPlayerEntity serverPlayerEntity, World world, @NotNull ItemStack itemStack, Hand hand, @NotNull CallbackInfoReturnable<ActionResult> cir) {
        String id = RegistryHelper.ofString(itemStack);

        if (Managers.getModuleManager().getInitializer(AntiBuildInitializer.class).config.getModel().anti.interact_item.id.contains(id)
                && !PermissionHelper.hasPermission(player.getUuid(), "fuji.anti_build.%s.bypass.%s".formatted("interact_item", id))
        ) {
            LocaleHelper.sendMessageByKey(player, "anti_build.disallow");
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    void $interactBlock(ServerPlayerEntity serverPlayerEntity, @NotNull World world, ItemStack itemStack, Hand hand, @NotNull BlockHitResult blockHitResult, @NotNull CallbackInfoReturnable<ActionResult> cir) {
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        String id = RegistryHelper.ofString(blockState);

        if (Managers.getModuleManager().getInitializer(AntiBuildInitializer.class).config.getModel().anti.interact_block.id.contains(id)
                && !PermissionHelper.hasPermission(player.getUuid(), "fuji.anti_build.%s.bypass.%s".formatted("interact_block", id))
        ) {
            LocaleHelper.sendMessageByKey(player, "anti_build.disallow");
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

}
