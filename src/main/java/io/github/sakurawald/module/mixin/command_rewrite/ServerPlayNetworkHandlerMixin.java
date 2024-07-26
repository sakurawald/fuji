package io.github.sakurawald.module.mixin.command_rewrite;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.structure.RegexRewriteEntry;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 1000 - 500)
public class ServerPlayNetworkHandlerMixin {

    @ModifyVariable(method = "executeCommand", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    public String $execute(@NotNull String string) {
        for (RegexRewriteEntry entry : Configs.configHandler.model().modules.command_rewrite.regex) {
            if (string.matches(entry.regex)) {
                return string.replaceAll(entry.regex, entry.replacement);
            }
        }

        return string;
    }
}
