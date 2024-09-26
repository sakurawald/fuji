package io.github.sakurawald.core.command.argument.adapter.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.core.command.argument.wrapper.impl.GameProfileCollection;
import lombok.SneakyThrows;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class GameProfileArgumentTypeAdapter extends BaseArgumentTypeAdapter {

    @Override
    public boolean match(Type type) {
        return GameProfileCollection.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return GameProfileArgumentType.gameProfile();
    }

    @SneakyThrows(CommandSyntaxException.class)
    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        return new GameProfileCollection(GameProfileArgumentType.getProfileArgument(context,parameter.getName()));
    }
}
