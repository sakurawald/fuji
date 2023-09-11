package fun.sakurawald.mixin.chat_style;

import fun.sakurawald.module.chat_style.ChatStyleModule;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1001)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "method_45064(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/lang/Void;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;)V")
            , locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void handleChat(PlayerChatMessage playerChatMessage, CompletableFuture<?> completableFuture, CompletableFuture<?> completableFuture2, Void arg3, CallbackInfo ci, PlayerChatMessage playerChatMessage2) {
        String message = playerChatMessage2.decoratedContent().getString();
        ChatStyleModule.handleChatMessage(player, message);
        ci.cancel();
    }
}
