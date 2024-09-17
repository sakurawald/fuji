package io.github.sakurawald.module.mixin.command_event;

import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.service.command_executor.CommandExecutor;
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
        CommandExecutor.executeSpecializedCommand(player, Configs.configHandler.getModel().modules.command_event.event.on_player_death.command_list);
    }

}
