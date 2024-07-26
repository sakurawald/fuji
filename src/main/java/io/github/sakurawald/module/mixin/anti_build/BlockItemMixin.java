package io.github.sakurawald.module.mixin.anti_build;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.util.minecraft.IdentifierHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.PermissionHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "canPlace", at = @At("RETURN"), cancellable = true)
    public void $canPlace(@NotNull ItemPlacementContext itemPlacementContext, BlockState blockState, @NotNull CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = itemPlacementContext.getPlayer();
        if (!(player instanceof ServerPlayerEntity)) return;

        String id = IdentifierHelper.ofString(itemPlacementContext.getStack());
        if (Configs.configHandler.model().modules.anti_build.anti.place_block.id.contains(id)
                && !PermissionHelper.hasPermission((ServerPlayerEntity) player, "fuji.anti_build.%s.bypass.%s".formatted("place_block", id))
        ) {
            MessageHelper.sendMessageToPlayerEntity(player, "anti_build.disallow");
            cir.setReturnValue(false);
        }
    }
}
