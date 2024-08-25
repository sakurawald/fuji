package io.github.sakurawald.module.mixin.command_event;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.service.command_executor.CommandExecutor;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerListManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    void onPlayerJoined(ClientConnection clientConnection, @NotNull ServerPlayerEntity player, ConnectedClientData connectedClientData, CallbackInfo ci) {

        CommandExecutor.executeSpecializedCommand(player, Configs.configHandler.model().modules.command_event.event.on_player_joined.command_list);

        if (player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) < 1) {
            CommandExecutor.executeSpecializedCommand(player, Configs.configHandler.model().modules.command_event.event.on_player_first_joined.command_list);
        }
    }

}
