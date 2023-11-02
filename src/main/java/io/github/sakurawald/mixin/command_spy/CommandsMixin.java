package io.github.sakurawald.mixin.command_spy;

import com.mojang.brigadier.ParseResults;
import io.github.sakurawald.Fuji;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Commands.class)

public class CommandsMixin {

    // If you issue "///abcdefg", then commandLine = "//abcdefg"
    @Inject(method = "performCommand", at = @At("HEAD"))
    public void $performCommand(ParseResults<CommandSourceStack> parseResults, String commandLine, CallbackInfoReturnable<Integer> cir) {
        ServerPlayer player = parseResults.getContext().getSource().getPlayer();
        if (player == null) return;

        // fix: fabric console will not log the command issue
        Fuji.log.info("{} issued server command: {}", player.getGameProfile().getName(), commandLine);
    }
}
