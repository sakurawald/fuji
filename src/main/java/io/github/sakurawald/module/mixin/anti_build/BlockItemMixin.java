package io.github.sakurawald.module.mixin.anti_build;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.util.IdentifierUtil;
import io.github.sakurawald.util.MessageUtil;
import io.github.sakurawald.util.PermissionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "canPlace", at = @At("RETURN"), cancellable = true)
    public void $canPlace(ItemPlacementContext itemPlacementContext, BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = itemPlacementContext.getPlayer();
        if (!(player instanceof ServerPlayerEntity)) return;

        String id = IdentifierUtil.getItemStackIdentifier(itemPlacementContext.getStack());
        if (Configs.configHandler.model().modules.anti_build.anti.place_block.id.contains(id)
                && !PermissionUtil.hasPermission((ServerPlayerEntity) player, "fuji.anti_build.%s.bypass.%s".formatted("place_block", id))
        ) {
            MessageUtil.sendMessageToPlayerEntity(player, "anti_build.disallow");
            cir.setReturnValue(false);
        }
    }
}
