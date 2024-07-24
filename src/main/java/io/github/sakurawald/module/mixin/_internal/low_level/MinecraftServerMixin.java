package io.github.sakurawald.module.mixin._internal.low_level;

import io.github.sakurawald.util.minecraft.ServerHelper;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.sakurawald.util.LogUtil.LOGGER;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void $init(CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        LOGGER.debug("MinecraftServerMixin: $init: {}", server);
        ServerHelper.setServer(server);
    }
}
