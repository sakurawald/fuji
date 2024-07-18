package io.github.sakurawald.module.mixin.command_rewrite;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.command_rewrite.CommandRewriteEntry;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 1000 - 500)
@Slf4j
public class ServerPlayNetworkHandlerMixin {

    @ModifyVariable(method = "executeCommand", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    public String $execute(String string) {
        for (CommandRewriteEntry rule : Configs.configHandler.model().modules.command_rewrite.rules) {
            if (string.matches(rule.from)) {
                return string.replaceAll(rule.from, rule.to);
            }
        }

        return string;
    }
}
