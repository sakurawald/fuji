package io.github.sakurawald.module.mixin._internal.low_level.command;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.sakurawald.command.interfaces.CommandContextBuilderAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandDispatcher.class)
public class CommandDispatcherMixin<S> {

    // apply patch: https://github.com/Mojang/brigadier/pull/142
    @Inject(method = "parseNodes", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;parseNodes(Lcom/mojang/brigadier/tree/CommandNode;Lcom/mojang/brigadier/StringReader;Lcom/mojang/brigadier/context/CommandContextBuilder;)Lcom/mojang/brigadier/ParseResults;", shift = At.Shift.BEFORE, ordinal = 0), remap = false)
    void passChildContextAfterRedirect(CommandNode<S> node, StringReader originalReader, CommandContextBuilder<S> contextSoFar, CallbackInfoReturnable<ParseResults<S>> cir, @Local(ordinal = 1) CommandContextBuilder<S> context, @Local(ordinal = 2) LocalRef<CommandContextBuilder<S>> childContext) {
        CommandContextBuilderAccessor<S> accessor = (CommandContextBuilderAccessor<S>) childContext.get();
        accessor.fuji$withArguments(context.getArguments());
    }

}
