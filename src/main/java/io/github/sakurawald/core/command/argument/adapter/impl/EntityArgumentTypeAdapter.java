package io.github.sakurawald.core.command.argument.adapter.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import lombok.SneakyThrows;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class EntityArgumentTypeAdapter extends BaseArgumentTypeAdapter {
    @Override
    public boolean match(Type type) {
        return Entity.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return EntityArgumentType.entity();
    }

    @SneakyThrows(CommandSyntaxException.class)
    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        return EntityArgumentType.getEntity(context,parameter.getName());
    }
}
