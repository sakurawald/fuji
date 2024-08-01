package io.github.sakurawald.module.mixin.tab_list.sort;

import io.github.sakurawald.util.LogUtil;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListS2CPacket.class)
public class DisableTheClientSideToShowPlayersInTabListMixin {

    @Inject(method = "entryFromPlayer", at = @At("RETURN"), cancellable = true)
    private static void removeAddPlayerAction(@NotNull CallbackInfoReturnable<PlayerListS2CPacket> cir) {
        // remove the vanilla minecraft `Action.UPDATE_LISTED` so that the player of the packet will not be listed in the client-side's tab list.
        PlayerListS2CPacket original = cir.getReturnValue();
        original.getActions().remove(PlayerListS2CPacket.Action.UPDATE_LISTED);
        cir.setReturnValue(original);
    }

}
