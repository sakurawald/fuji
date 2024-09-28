package io.github.sakurawald.module.mixin.command_spy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.module.initializer.command_spy.CommandSpyInitializer;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CommandDispatcher.class, remap = false)
public class CommandsMixin {

    // Listen on the parse() instead of execute(), to ensure that we will not miss some commands. (it's possible that we spy on some commands that just parsed without execution)
    @Inject(method = "parse(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)Lcom/mojang/brigadier/ParseResults;", at = @At("HEAD"))
    public void spyOnCommandParse(StringReader command, Object source, CallbackInfoReturnable<ParseResults<Object>> cir) {
        // verify
        if (!(source instanceof ServerCommandSource serverCommandSource)) return;
        if (!CommandSpyInitializer.config.getModel().spy_on_console
            && serverCommandSource.getPlayer() == null) return;

        // spy
        String name = serverCommandSource.getName();
        String string = command.getString();
        LogUtil.info("{} issued server command: /{}", name, string);
    }
}
