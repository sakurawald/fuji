package io.github.sakurawald.module.mixin.chat.spy;

import io.github.sakurawald.core.auxiliary.LogUtil;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow
    public abstract ServerPlayerEntity getPlayer();

    @Inject(method = "onChatMessage", at = @At(value = "HEAD"))
    public void modifyChatMessageSentByPlayers(ChatMessageC2SPacket chatMessageC2SPacket, CallbackInfo ci) {
        LogUtil.info("[chat spy] <{}> {}", getPlayer().getGameProfile().getName(), chatMessageC2SPacket.chatMessage());
    }
}
