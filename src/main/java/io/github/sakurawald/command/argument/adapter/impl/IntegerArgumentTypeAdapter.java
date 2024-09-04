package io.github.sakurawald.command.argument.adapter.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class IntegerArgumentTypeAdapter extends BaseArgumentTypeAdapter {

    @Override
    public boolean match(Type type) {
        return  int.class.equals(type) || Integer.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return IntegerArgumentType.integer();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        return IntegerArgumentType.getInteger(context, parameter.getName());
    }
}
