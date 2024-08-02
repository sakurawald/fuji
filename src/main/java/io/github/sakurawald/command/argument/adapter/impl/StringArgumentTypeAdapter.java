package io.github.sakurawald.command.argument.adapter.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.argument.adapter.interfaces.AbstractArgumentTypeAdapter;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class StringArgumentTypeAdapter extends AbstractArgumentTypeAdapter {

    @Override
    public boolean match(Type type) {
        return String.class.equals(type);
    }

    @Override
    public ArgumentType<?> makeArgumentType() {
        return StringArgumentType.string();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        return StringArgumentType.getString(context, parameter.getName());
    }
}
