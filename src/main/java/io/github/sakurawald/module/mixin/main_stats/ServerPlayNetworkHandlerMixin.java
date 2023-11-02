package io.github.sakurawald.module.mixin.main_stats;


import io.github.sakurawald.module.initializer.main_stats.MainStats;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerCommonPacketListenerImpl;onDisconnect(Lnet/minecraft/network/chat/Component;)V"), method = "onDisconnect")
    private void $disconnect(Component reason, CallbackInfo info) {
        String uuid = player.getUUID().toString();
        MainStats.uuid2stats.remove(uuid);
    }
}
