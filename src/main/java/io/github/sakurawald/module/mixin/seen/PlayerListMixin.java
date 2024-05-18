package io.github.sakurawald.module.mixin.seen;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.seen.SeenModule;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)

public abstract class PlayerListMixin {

    @Unique
    private SeenModule module = ModuleManager.getInitializer(SeenModule.class);

    @Inject(method = "remove", at = @At("TAIL"))
    private void remove(ServerPlayerEntity player, CallbackInfo ci) {
        module.getData().model().player2seen.put(player.getGameProfile().getName(), System.currentTimeMillis());
        module.getData().saveToDisk();
    }

}
