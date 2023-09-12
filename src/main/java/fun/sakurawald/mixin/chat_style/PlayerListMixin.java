package fun.sakurawald.mixin.chat_style;

import fun.sakurawald.module.better_fake_player.BetterFakePlayerModule;
import fun.sakurawald.module.chat_style.ChatStyleModule;
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
        ChatStyleModule.CHAT_HISTORY.forEach(player::sendMessage);
    }
}
