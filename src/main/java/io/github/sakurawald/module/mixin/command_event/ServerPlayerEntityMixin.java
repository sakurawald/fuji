package io.github.sakurawald.module.mixin.command_event;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.structure.CommandExecutor;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onPlayerDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        CommandExecutor.executeCommandsAsConsoleWithContext(player, Configs.configHandler.model().modules.command_event.event.on_player_death.command_list);
    }

}
