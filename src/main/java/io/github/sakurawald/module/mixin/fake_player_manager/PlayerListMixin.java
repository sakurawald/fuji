package io.github.sakurawald.module.mixin.fake_player_manager;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.fake_player_manager.FakePlayerManagerModule;
import io.github.sakurawald.util.CarpetUtil;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerListMixin {
    @Unique
    private static final FakePlayerManagerModule module = ModuleManager.getInitializer(FakePlayerManagerModule.class);

    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void $onPlayerConnect(ClientConnection connection, ServerPlayerEntity serverPlayer, ConnectedClientData commonListenerCookie, CallbackInfo ci) {
        if (CarpetUtil.isFakePlayer(serverPlayer)) return;
        if (module.hasFakePlayers(serverPlayer)) {
            module.renewFakePlayers(serverPlayer);
        }
    }
}
