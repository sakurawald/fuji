package io.github.sakurawald.module.mixin.bypass_move_speed;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("unused")
@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayerEntity player;

    @ModifyExpressionValue(
            method = "onPlayerMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;isHost()Z"
            )
    )
    public boolean disablePlayerMoveTooQuickly(boolean original) {
        return true;
    }

    @ModifyExpressionValue(
            method = "onVehicleMove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;isHost()Z"
            )
    )
    public boolean disableVehicleMoveTooQuickly(boolean original) {
        return true;
    }
}
