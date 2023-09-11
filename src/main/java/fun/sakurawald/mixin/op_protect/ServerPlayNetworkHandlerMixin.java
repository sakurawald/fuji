package fun.sakurawald.mixin.op_protect;


import fun.sakurawald.ModMain;
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

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;disconnect()V"), method = "onDisconnect")
    private void $disconnect(Component reason, CallbackInfo info) {
        if (ModMain.SERVER.getPlayerList().isOp(player.getGameProfile())) {
            ModMain.LOGGER.info("Op Protect: deop " + player.getGameProfile());
            ModMain.SERVER.getPlayerList().deop(player.getGameProfile());
        }
    }
}
