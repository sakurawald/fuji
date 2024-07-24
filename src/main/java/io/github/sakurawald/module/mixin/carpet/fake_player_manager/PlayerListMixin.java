package io.github.sakurawald.module.mixin.carpet.fake_player_manager;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.carpet.fake_player_manager.FakePlayerManagerInitializer;
import io.github.sakurawald.util.minecraft.EntityHelper;
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
    private static final FakePlayerManagerInitializer module = ModuleManager.getInitializer(FakePlayerManagerInitializer.class);

    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void $onPlayerConnect(ClientConnection connection, ServerPlayerEntity serverPlayer, ConnectedClientData commonListenerCookie, CallbackInfo ci) {
        if (EntityHelper.isNonRealPlayer(serverPlayer)) return;
        if (module.hasFakePlayers(serverPlayer)) {
            module.renewFakePlayers(serverPlayer);
        }
    }
}
