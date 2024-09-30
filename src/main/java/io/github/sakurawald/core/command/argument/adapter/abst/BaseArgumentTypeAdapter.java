package io.github.sakurawald.core.command.argument.adapter.abst;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.manager.Managers;
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

    protected abstract boolean match(Type type);

    public RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(Parameter parameter) {
        String argumentName = parameter.getName();
        ArgumentType<?> argumentType = this.makeArgumentType();
        return CommandManager.argument(argumentName, argumentType);
    }

    protected abstract ArgumentType<?> makeArgumentType();

    protected abstract Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter);

    public Object makeParameterObject(CommandContext<ServerCommandSource> ctx, Parameter parameter) {
        Object argumentObject = this.makeArgumentObject(ctx, parameter);
        return box(parameter, argumentObject);
    }

    public boolean verifyCommandSource(CommandContext<ServerCommandSource> context) {
        return true;
    }

    private static Type unbox(Parameter parameter) {
        if (parameter.getType().equals(Optional.class)) {
            ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
            return parameterizedType.getActualTypeArguments()[0];
        }

        return parameter.getType();
    }

    private static Object box(Parameter parameter, Object value) {
        // pack the type
        if (parameter.getType().equals(Optional.class)) {
            return Optional.of(value);
        }

        return value;
    }

    public static BaseArgumentTypeAdapter getAdapter(Parameter parameter) {
        Type type = unbox(parameter);

        for (BaseArgumentTypeAdapter adapter : adapters) {
            if (adapter.match(type)) {
                return adapter;
            }
        }

        throw new RuntimeException("No adapters match the argument type: " + type.getTypeName());
    }

}
