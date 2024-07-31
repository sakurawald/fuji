package io.github.sakurawald.command.adapter.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.adapter.ArgumentTypeAdapter;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;


@SuppressWarnings("unused")
public class FloatArgumentTypeAdapter extends ArgumentTypeAdapter {
    @Override
    public boolean match(Type type) {
        return Float.class.equals(type) || float.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return FloatArgumentType.floatArg();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        return FloatArgumentType.getFloat(context, parameter.getName());
    }
}
