package io.github.sakurawald.command.argument.adapter.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.argument.adapter.interfaces.AbstractArgumentTypeAdapter;
import io.github.sakurawald.command.argument.wrapper.Dimension;
import io.github.sakurawald.util.minecraft.CommandHelper;
import lombok.SneakyThrows;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class DimensionArgumentTypeAdapter extends AbstractArgumentTypeAdapter {
    @Override
    public boolean match(Type type) {
        return Dimension.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return DimensionArgumentType.dimension();
    }

    @Override
    public RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(Parameter parameter) {
        return super.makeRequiredArgumentBuilder(parameter).suggests(CommandHelper.Suggestion.dimension());
    }

    @SneakyThrows
    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {

        return new Dimension(DimensionArgumentType.getDimensionArgument(context, parameter.getName()));
    }
}
