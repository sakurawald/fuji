package fun.sakurawald.mixin.better_fake_player;

import fun.sakurawald.module.better_fake_player.BetterFakePlayerModule;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Inject(at = @At(value = "TAIL"), method = "placeNewPlayer")
    private void $placeNewPlayer(Connection connection, ServerPlayer player, CallbackInfo info) {
        if (BetterFakePlayerModule.isFakePlayer(player)) return;
        if (BetterFakePlayerModule.hasFakePlayers(player)) {
            BetterFakePlayerModule.renewFakePlayers(player);
        }
    }
}
