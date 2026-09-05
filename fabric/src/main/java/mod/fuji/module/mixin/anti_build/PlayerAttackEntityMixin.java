package mod.fuji.module.mixin.anti_build;

import mod.fuji.core.auxiliary.minecraft.PlayerHelper;
import mod.fuji.core.auxiliary.minecraft.RegistryHelper;
import mod.fuji.module.initializer.anti_build.AntiBuildInitializer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

#if MC_VER < MC_26_1
import net.minecraft.server.level.ServerPlayer;
@Mixin(ServerPlayer.class)
#elif MC_VER >= MC_26_1
@Mixin(Player.class)
#endif
public class PlayerAttackEntityMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    void handleAttackEntity(Entity entity, CallbackInfo ci) {
        PlayerHelper.Kind.ifServerPlayerEntity(this, player -> {
            var config = AntiBuildInitializer.config.model().getAntiActionTypes().getAttackEntity();
            if (!config.isEnable()) return;

            String id = RegistryHelper.getIdAsString(entity);
            AntiBuildInitializer.processAntiBuildAction(player, "attack_entity", config.getId(), id, ci::cancel, () -> true);
        });
    }

}
