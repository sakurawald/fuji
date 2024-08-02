package io.github.sakurawald.command.argument.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractArgumentTypeAdapter {

    private static final List<AbstractArgumentTypeAdapter> adapters = new ArrayList<>();

    public static void registerAdapters() {
        Reflections reflections = new Reflections(Fuji.class.getPackage().getName());
        reflections.getSubTypesOf(AbstractArgumentTypeAdapter.class).forEach(o -> {
            try {
                Constructor<? extends AbstractArgumentTypeAdapter> constructor = o.getDeclaredConstructor();
                AbstractArgumentTypeAdapter instance = constructor.newInstance();
                adapters.add(instance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }

    public abstract boolean match(Type type);

    public RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(Parameter parameter) {
        String argumentName = parameter.getName();
        ArgumentType<?> argumentType = this.makeArgumentType();
        return CommandManager.argument(argumentName, argumentType);
    }

    protected abstract ArgumentType<?> makeArgumentType();

    public abstract Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter);

    public boolean validateCommandSource(CommandContext<ServerCommandSource> context) {
        return true;
    }

    private static Type unpackType(Parameter parameter) {
        if (parameter.getType().equals(Optional.class)) {
            ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
            return parameterizedType.getActualTypeArguments()[0];
        }

        return parameter.getType();
    }

    public static AbstractArgumentTypeAdapter getAdapter(Parameter parameter) {
        Type type = unpackType(parameter);

        for (AbstractArgumentTypeAdapter adapter : adapters) {
            if (adapter.match(type)) {
                return adapter;
            }
        }

        throw new RuntimeException("No adapters match the argument type: " + type.getTypeName());
    }

}
