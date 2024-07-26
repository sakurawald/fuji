package io.github.sakurawald.module.mixin.anti_build;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.util.minecraft.IdentifierHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.PermissionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    void $interact(@NotNull PlayerEntity player, Hand hand, @NotNull CallbackInfoReturnable<ActionResult> cir){
        Entity entity = (Entity) (Object) this;
        String id = IdentifierHelper.ofString(entity);

        if (Configs.configHandler.model().modules.anti_build.anti.interact_entity.id.contains(id)
                && !PermissionHelper.hasPermission(player, "fuji.anti_build.%s.bypass.%s".formatted("interact_entity", id))
        ) {

            if (hand == Hand.MAIN_HAND) {
                MessageHelper.sendMessageToPlayerEntity(player, "anti_build.disallow");
            }

            cir.setReturnValue(ActionResult.FAIL);
        }
    }

}
