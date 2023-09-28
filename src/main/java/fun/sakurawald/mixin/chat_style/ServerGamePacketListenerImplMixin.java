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

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1001)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "broadcastChatMessage", at = @At(value = "HEAD"), cancellable = true)
    public void handleChat(PlayerChatMessage playerChatMessage, CallbackInfo ci) {
        ChatStyleModule.handleChatMessage(player, playerChatMessage.decoratedContent().getString());
        ci.cancel();
    }
}
