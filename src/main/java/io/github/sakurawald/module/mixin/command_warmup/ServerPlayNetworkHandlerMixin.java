package io.github.sakurawald.module.mixin.command_warmup;

import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.structure.CommandWarmupTicket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(net.minecraft.server.network.ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow
    public abstract ServerPlayerEntity getPlayer();

    @Unique
    public int getMs(@NotNull String commandLine) {
        for (Map.Entry<String, Integer> entry : Configs.configHandler.getModel().modules.command_warmup.regex2ms.entrySet()) {
            if (!commandLine.matches(entry.getKey())) continue;
            return entry.getValue();
        }

        return 0;
    }

    @Inject(method = "onCommandExecution", at = @At("HEAD"), cancellable = true)
    public void interceptCommandUsagePackets(@NotNull CommandExecutionC2SPacket commandExecutionC2SPacket, @NotNull CallbackInfo ci) {
        ServerPlayerEntity player = getPlayer();
        String command = commandExecutionC2SPacket.comp_808();

        int ms = getMs(command);
        if (ms > 0) {
            Managers.getBossBarManager().addTicket(CommandWarmupTicket.of(player, command, ms));
            ci.cancel();
        }
    }
}
