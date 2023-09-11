package fun.sakurawald.mixin.chat_history;


import fun.sakurawald.module.chat_history.CachedMessage;
import fun.sakurawald.util.CarpetUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;
import java.util.function.Predicate;


@Mixin(PlayerList.class)
public class PlayerManagerMixin {
    @Inject(method = "broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Ljava/util/function/Function;Z)V",
            at = @At("HEAD"))
    public void cacheGameMessage(Component message, Function<ServerPlayer, Component> playerMessageFactory, boolean overlay, CallbackInfo ci) {
        if (overlay) return;
        CachedMessage.MESSAGE_CACHE.add(new CachedMessage(message));
    }

    @Inject(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            at = @At("HEAD"))
    public void cachePlayerMessage(PlayerChatMessage message, Predicate<ServerPlayer> filterPredicate, @Nullable ServerPlayer sender, ChatType.Bound params, CallbackInfo ci) {
        var decoratedMessage = params.decorate(message.decoratedContent());
        CachedMessage.MESSAGE_CACHE.add(new CachedMessage(decoratedMessage));
    }

    @Inject(method = "placeNewPlayer", at = @At(value = "TAIL", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onSpawn()V"))
    public void sendCachedMessages(Connection connection, ServerPlayer player, CallbackInfo ci) {
        if (CarpetUtil.isFakePlayer(player)) return;
        for (var message : CachedMessage.MESSAGE_CACHE) {
            message.send(player);
        }
    }
}
