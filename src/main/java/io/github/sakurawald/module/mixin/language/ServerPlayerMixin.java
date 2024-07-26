package io.github.sakurawald.module.mixin.language;

import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayerEntity.class)

public abstract class ServerPlayerMixin {

    @Inject(method = "setClientOptions", at = @At("HEAD"))
    public void $setClientOptions(@NotNull SyncedClientOptions clientInformation, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        MessageHelper.getPlayer2lang().put(player.getGameProfile().getName(), clientInformation.comp_1951());
    }
}
