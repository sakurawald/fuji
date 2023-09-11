package fun.sakurawald.mixin.chat_history;

import fun.sakurawald.module.chat_history.ChatHistoryModule;
import fun.sakurawald.util.CarpetUtil;
import net.kyori.adventure.text.Component;
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
        if (CarpetUtil.isFakePlayer(player)) return;

        for (Component component : ChatHistoryModule.CACHE) {
            player.sendMessage(component);
        }
    }
}
