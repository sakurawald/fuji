package fun.sakurawald.mixin.chat_history;


import fun.sakurawald.module.chat_history.CachedMessage;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;
import java.util.function.Predicate;


@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Z)V",
            at = @At("HEAD"))
    public void cacheGameMessage(Text message, Function<ServerPlayerEntity, Text> playerMessageFactory, boolean overlay, CallbackInfo ci) {
        if (overlay) return;
        CachedMessage.MESSAGE_CACHE.add(new CachedMessage(message));
    }

    @Inject(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V",
            at = @At("HEAD"))
    public void cachePlayerMessage(SignedMessage message, Predicate<ServerPlayerEntity> filterPredicate, @Nullable ServerPlayerEntity sender, MessageType.Parameters params, CallbackInfo ci) {
        var decoratedMessage = params.applyChatDecoration(message.getContent());
        CachedMessage.MESSAGE_CACHE.add(new CachedMessage(decoratedMessage));
    }

    @Inject(method = "onPlayerConnect", at = @At(value = "TAIL", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onSpawn()V"))
    public void sendCachedMessages(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        for (var message : CachedMessage.MESSAGE_CACHE) {
            message.send(player);
        }
    }
}
