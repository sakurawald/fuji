package fun.sakurawald.mixin.newbie_welcome;

import fun.sakurawald.module.newbie_welcome.NewbieWelcomeModule;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        if (player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) < 1) {
            NewbieWelcomeModule.welcomeNewbiePlayer(player);
        }
    }
}
