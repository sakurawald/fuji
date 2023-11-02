package io.github.sakurawald.module.mixin.chat;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.chat.ChatModule;
import io.github.sakurawald.util.CarpetUtil;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerList.class, priority = 999)
public abstract class PlayerListMixin {

    @Unique
    private static final ChatModule module = ModuleManager.getInitializer(ChatModule.class);

    @Inject(at = @At(value = "TAIL"), method = "placeNewPlayer")
    private void $placeNewPlayer(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        if (CarpetUtil.isFakePlayer(serverPlayer)) return;
        module.getChatHistory().forEach(serverPlayer::sendMessage);
    }
}
