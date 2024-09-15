package io.github.sakurawald.core.command.argument.adapter.abst;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.manager.Managers;
import lombok.SneakyThrows;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseArgumentTypeAdapter {

    private static final List<BaseArgumentTypeAdapter> adapters = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static void registerAdapters() {
        ReflectionUtil.getGraph(ReflectionUtil.ARGUMENT_TYPE_ADAPTER_GRAPH_FILE_NAME)
            .stream()
            .filter(className -> Managers.getModuleManager().shouldWeEnableThis(className))
            .forEach(className -> {
                try {
                    Class<? extends BaseArgumentTypeAdapter> clazz = (Class<? extends BaseArgumentTypeAdapter>) Class.forName(className);
                    Constructor<? extends BaseArgumentTypeAdapter> constructor = clazz.getDeclaredConstructor();
                    BaseArgumentTypeAdapter instance = constructor.newInstance();
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

    public static BaseArgumentTypeAdapter getAdapter(Parameter parameter) {
        Type type = unpackType(parameter);

        for (BaseArgumentTypeAdapter adapter : adapters) {
            if (adapter.match(type)) {
                return adapter;
            }
        }

        throw new RuntimeException("No adapters match the argument type: " + type.getTypeName());
    }

}
