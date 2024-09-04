package io.github.sakurawald.command.argument.adapter.interfaces;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.classgraph.ClassInfo;
import io.github.sakurawald.auxiliary.ReflectionUtil;
import io.github.sakurawald.module.common.manager.impl.module.ModuleManager;
import io.github.sakurawald.module.common.manager.Managers;
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

public abstract class AbstractArgumentTypeAdapter {

    private static final List<AbstractArgumentTypeAdapter> adapters = new ArrayList<>();

    @SneakyThrows
    public static void registerAdapters() {
        ModuleManager moduleManager = Managers.getModuleManager();

        for (ClassInfo classInfo : ReflectionUtil.getClassInfoScanResult().getSubclasses(AbstractArgumentTypeAdapter.class)) {
            String className = classInfo.getName();

            // skip if the module path is not enabled.
            if (!moduleManager.shouldWeEnableThis(className)) continue;

            Class<? extends AbstractArgumentTypeAdapter> clazz = (Class<? extends AbstractArgumentTypeAdapter>) Class.forName(classInfo.getName());
            Constructor<? extends AbstractArgumentTypeAdapter> constructor = clazz.getDeclaredConstructor();
            AbstractArgumentTypeAdapter instance = constructor.newInstance();
            adapters.add(instance);
        }
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
