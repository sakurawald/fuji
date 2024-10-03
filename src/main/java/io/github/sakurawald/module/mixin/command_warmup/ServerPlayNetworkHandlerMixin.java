package io.github.sakurawald.module.mixin.command_warmup;

import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.command_warmup.CommandWarmupInitializer;
import io.github.sakurawald.module.initializer.command_warmup.structure.CommandWarmupEntry;
import io.github.sakurawald.module.initializer.command_warmup.structure.CommandWarmupTicket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 1000 - 500)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onCommandExecution", at = @At("HEAD"), cancellable = true)
    public void interceptCommandUsagePackets(@NotNull CommandExecutionC2SPacket commandExecutionC2SPacket, @NotNull CallbackInfo ci) {
        String command = commandExecutionC2SPacket.comp_808();

        var config = CommandWarmupInitializer.config.getModel();

        for (CommandWarmupEntry entry : config.entries) {
            // cancel the usage of command, if a warmup entry matches.
            if (command.matches(entry.getCommand().getRegex())) {
                Managers.getBossBarManager().addTicket(CommandWarmupTicket.make(player, command, entry));

                if (config.warn_for_move) {
                    LocaleHelper.sendActionBarByKey(player, "command_warmup.warn_for_move", entry.getInterruptible().getInterruptDistance());
                }

                ci.cancel();
                break;
            }
        }

    }
}
