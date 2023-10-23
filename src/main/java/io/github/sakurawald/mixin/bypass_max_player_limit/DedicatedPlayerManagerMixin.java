package io.github.sakurawald.mixin.bypass_max_player_limit;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("unused")
@Mixin(DedicatedPlayerList.class)
public abstract class DedicatedPlayerManagerMixin {

    @ModifyExpressionValue(
            method = "canBypassPlayerLimit",
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/server/players/ServerOpList;canBypassPlayerLimit(Lcom/mojang/authlib/GameProfile;)Z"
            )
    )
    public boolean disablePlayerLimit(boolean original, GameProfile profile) {
        return true;
    }

}
