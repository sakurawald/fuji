package io.github.sakurawald.command.adapter.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.adapter.ArgumentTypeAdapter;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class DoubleArgumentTypeAdapter extends ArgumentTypeAdapter {
    @Override
    public boolean match(Type type) {
        return double.class.equals(type) || Double.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return DoubleArgumentType.doubleArg();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        return DoubleArgumentType.getDouble(context, parameter.getName());
    }
}
