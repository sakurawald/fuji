package io.github.sakurawald.command.interfaces;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;

import java.util.Map;

public interface CommandContextBuilderAccessor<S> {

    Map<String, ParsedArgument<S, ?>> getArguments();

    CommandContextBuilder<S> withArguments(Map<String, ParsedArgument<S, ?>> arguments);
}
