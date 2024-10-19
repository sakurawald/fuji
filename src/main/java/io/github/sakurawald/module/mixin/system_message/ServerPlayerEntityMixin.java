package io.github.sakurawald.module.mixin.system_message;

import io.github.sakurawald.module.initializer.system_message.SystemMessageInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayerEntity.class, priority = 1000 - 500)
public class ServerPlayerEntityMixin {

    @Inject(method = "sendMessageToClient", at = @At("HEAD"), cancellable = true)
    void cancelText(Text text, boolean bl, CallbackInfo ci) {
        if (text == SystemMessageInitializer.CANCEL_TEXT_SENDING_MARKER) {
            ci.cancel();
        }
    }

}
