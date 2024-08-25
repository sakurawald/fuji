package io.github.sakurawald.module.mixin.command_event;


import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.service.command_executor.CommandExecutor;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(at = @At("HEAD"), method = "onDisconnected")
    private void onPlayerLeft(DisconnectionInfo disconnectionInfo, CallbackInfo ci) {
        CommandExecutor.executeSpecializedCommand(player, Configs.configHandler.model().modules.command_event.event.on_player_left.command_list);
    }
}
