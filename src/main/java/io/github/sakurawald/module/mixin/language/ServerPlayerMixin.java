package io.github.sakurawald.module.mixin.language;

import io.github.sakurawald.module.initializer.teleport_warmup.ServerPlayerAccessor;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayer.class)

public abstract class ServerPlayerMixin implements ServerPlayerAccessor {

    @Inject(method = "updateOptions", at = @At("HEAD"))
    public void updateOptions(ClientInformation clientInformation, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        MessageUtil.getPlayer2lang().put(player.getGameProfile().getName(), clientInformation.language());
    }
}
