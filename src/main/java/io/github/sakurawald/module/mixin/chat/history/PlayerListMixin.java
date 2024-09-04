package io.github.sakurawald.module.mixin.chat.history;

import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.chat.history.ChatHistoryInitializer;
import io.github.sakurawald.core.auxiliary.minecraft.EntityHelper;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerManager.class, priority = 999)
public abstract class PlayerListMixin {

    @Unique
    private static final ChatHistoryInitializer module = Managers.getModuleManager().getInitializer(ChatHistoryInitializer.class);

    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void sendChatHistoryToNewJoinedPlayer(ClientConnection connection, @NotNull ServerPlayerEntity serverPlayer, ConnectedClientData commonListenerCookie, CallbackInfo ci) {
        if (EntityHelper.isNonRealPlayer(serverPlayer)) return;

        module.getChatHistory().forEach(serverPlayer::sendMessage);
    }
}
