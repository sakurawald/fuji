package io.github.sakurawald.module.mixin._internal.event;

import com.mojang.brigadier.ParseResults;
import io.github.sakurawald.common.event.PreCommandExecuteEvent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class CommandManagerMixin {

    // If you issue "///abcdefg", then commandLine = "//abcdefg"
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    public void $execute(ParseResults<ServerCommandSource> parseResults, String string, CallbackInfo ci) {
        ActionResult result = PreCommandExecuteEvent.EVENT.invoker().interact(parseResults, string);
        if (result == ActionResult.FAIL) {
           ci.cancel();
        }
    }

}
