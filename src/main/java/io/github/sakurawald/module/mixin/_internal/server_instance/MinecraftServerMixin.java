package io.github.sakurawald.module.mixin._internal.server_instance;

import io.github.sakurawald.Fuji;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.sakurawald.Fuji.log;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void $init(CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        log.debug("MinecraftServerMixin: $init: " + server);
        Fuji.SERVER = server;
    }
}
