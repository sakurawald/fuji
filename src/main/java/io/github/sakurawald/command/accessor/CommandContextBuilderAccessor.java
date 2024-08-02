package io.github.sakurawald.command.accessor;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;

import java.util.Map;

public interface CommandContextBuilderAccessor<S> {

    Map<String, ParsedArgument<S, ?>> getArguments();

    @SuppressWarnings("UnusedReturnValue")
    CommandContextBuilder<S> fuji$withArguments(Map<String, ParsedArgument<S, ?>> arguments);
}
