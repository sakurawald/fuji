package io.github.sakurawald.module.mixin.skin;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.skin.SkinRestorer;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

@Mixin(PlayerManager.class)

public abstract class PlayerListMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    public abstract List<ServerPlayerEntity> getPlayerList();

    @Inject(method = "remove", at = @At("TAIL"))
    private void remove(ServerPlayerEntity player, CallbackInfo ci) {
        SkinRestorer.getSkinStorage().removeSkin(player.getUuid());
    }

    @Inject(method = "disconnectAllPlayers", at = @At("HEAD"))
    private void disconnectAllPlayers(CallbackInfo ci) {
        getPlayerList().forEach(player -> SkinRestorer.getSkinStorage().removeSkin(player.getUuid()));
    }

    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void onPlayerConnected(ClientConnection connection, ServerPlayerEntity serverPlayer, ConnectedClientData commonListenerCookie, CallbackInfo ci) {
        // if the player isn't a server player entity, it must be someone's fake player
        if (serverPlayer.getClass() != ServerPlayerEntity.class
                && Configs.configHandler.model().modules.fake_player_manager.use_local_random_skins_for_fake_player) {
            SkinRestorer.setSkinAsync(server, Collections.singleton(serverPlayer.getGameProfile()), () -> SkinRestorer.getSkinStorage().getRandomSkin(serverPlayer.getUuid()));
        }
    }
}
