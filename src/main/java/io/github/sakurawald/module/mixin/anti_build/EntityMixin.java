package io.github.sakurawald.module.mixin.anti_build;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.util.IdentifierUtil;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    void $interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        Entity entity = (Entity) (Object) this;
        String id = IdentifierUtil.getEntityTypeIdentifier(entity);

        if (Configs.configHandler.model().modules.anti_build.anti.interact_entity.id.contains(id)
                && !Permissions.check(player, "fuji.anti_build.%s.bypass.%s".formatted("interact_entity", id))
        ) {

            if (hand == Hand.MAIN_HAND) {
                MessageUtil.sendMessageToPlayerEntity(player, "anti_build.disallow");
            }

            cir.setReturnValue(ActionResult.FAIL);
        }
    }

}
