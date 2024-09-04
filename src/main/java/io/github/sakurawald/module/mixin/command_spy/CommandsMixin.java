package io.github.sakurawald.module.mixin.command_spy;

import com.mojang.brigadier.ParseResults;
import io.github.sakurawald.core.auxiliary.LogUtil;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)

public class CommandsMixin {

    // If you issue "///abcdefg", then commandLine = "//abcdefg"
    @Inject(method = "execute", at = @At("HEAD"))
    public void watchCommandExecution(@NotNull ParseResults<ServerCommandSource> parseResults, String string, CallbackInfo ci) {
        ServerPlayerEntity player = parseResults.getContext().getSource().getPlayer();
        if (player == null) return;

        // fix: fabric console will not log the command issue
        LogUtil.info("{} issued server command: /{}", player.getGameProfile().getName(), string);
    }
}
