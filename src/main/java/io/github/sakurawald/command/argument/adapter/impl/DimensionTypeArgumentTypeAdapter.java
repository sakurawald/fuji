package io.github.sakurawald.command.argument.adapter.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.argument.adapter.AbstractArgumentTypeAdapter;
import io.github.sakurawald.command.argument.adapter.wrapper.DimensionType;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class DimensionTypeArgumentTypeAdapter extends AbstractArgumentTypeAdapter {

    @Override
    public boolean match(Type type) {
        return DimensionType.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return StringArgumentType.greedyString();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        return new DimensionType(StringArgumentType.getString(context, parameter.getName()));
    }

    @Override
    public RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(Parameter parameter) {
        return super.makeRequiredArgumentBuilder(parameter).suggests(CommandHelper.Suggestion.dimensionType());
    }
}
