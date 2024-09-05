package io.github.sakurawald.core.command.argument.adapter.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.core.command.argument.wrapper.impl.Dimension;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import lombok.SneakyThrows;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class DimensionArgumentTypeAdapter extends BaseArgumentTypeAdapter {
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
            /*
             The DimensionArgumentType.dimension() will not suggest the new registered dimension types.
             Each time the server started, the dimensions will be shared with client and server.
             */
        return super.makeRequiredArgumentBuilder(parameter).suggests(CommandHelper.Suggestion.ofRegistryKey(RegistryKeys.DIMENSION));
    }

    @SneakyThrows
    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        return new Dimension(DimensionArgumentType.getDimensionArgument(context, parameter.getName()));
    }
}
