package io.github.sakurawald.module.mixin._internal.low_level.command;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import io.github.sakurawald.command.accessor.CommandContextBuilderAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(CommandContextBuilder.class)
public abstract class CommandContextBuilderMixin<S> implements CommandContextBuilderAccessor<S> {

    @Accessor(value = "arguments", remap = false)
    public abstract Map<String, ParsedArgument<S, ?>> getArguments();

    @Unique
    public CommandContextBuilder<S> fuji$withArguments(Map<String, ParsedArgument<S, ?>> arguments) {
        getArguments().putAll(arguments);
        CommandContextBuilder that = (CommandContextBuilder) (Object) this;
        return that;
    }
}
