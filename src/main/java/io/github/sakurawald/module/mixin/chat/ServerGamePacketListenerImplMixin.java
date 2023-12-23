package io.github.sakurawald.module.mixin.chat;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.chat.ChatModule;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1001)
public abstract class ServerGamePacketListenerImplMixin {
    @Unique
    private static final ChatModule module = ModuleManager.getInitializer(ChatModule.class);
    @Shadow
    public ServerPlayer player;

    @Inject(method = "broadcastChatMessage", at = @At(value = "HEAD"), cancellable = true)
    public void handleChat(PlayerChatMessage playerChatMessage, CallbackInfo ci) {
        module.broadcastChatMessage(player, playerChatMessage.decoratedContent().getString());
        ci.cancel();
    }
}
