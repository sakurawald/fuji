package io.github.sakurawald.module.mixin.command_warmup;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.common.structure.CommandWarmupTicket;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(net.minecraft.server.network.ServerPlayNetworkHandler.class)
@Slf4j
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow
    public abstract ServerPlayerEntity getPlayer();

    @Unique
    public int getMs(String commandLine) {
        for (Map.Entry<String, Integer> entry : Configs.configHandler.model().modules.command_warmup.regex2ms.entrySet()) {
            if (!commandLine.matches(entry.getKey())) continue;
            return entry.getValue();
        }

        return 0;
    }

    @Inject(method = "onCommandExecution", at = @At("HEAD"), cancellable = true)
    public void $execute(CommandExecutionC2SPacket commandExecutionC2SPacket, CallbackInfo ci) {
        ServerPlayerEntity player = getPlayer();
        String string = commandExecutionC2SPacket.comp_808();

        int ms = getMs(string);
        if (ms > 0) {
            BossBar bossbar = BossBar.bossBar(MessageUtil.ofText(player, "command_warmup.bossbar.name", string), 0f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);
            Managers.getBossBarManager().addTicket(new CommandWarmupTicket(bossbar, ms, player, string) {
                @Override
                public void onComplete() {
                    player.networkHandler.executeCommand(string);
                }
            });
            ci.cancel();
        }
    }
}
