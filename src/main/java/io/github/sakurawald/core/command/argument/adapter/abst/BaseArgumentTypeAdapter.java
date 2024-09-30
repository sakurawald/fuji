package io.github.sakurawald.core.command.argument.adapter.abst;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.command.argument.structure.Argument;
import io.github.sakurawald.core.manager.Managers;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Constructor;
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

    public RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(String argumentName) {
        ArgumentType<?> argumentType = this.makeArgumentType();
        return CommandManager.argument(argumentName, argumentType);
    }

    protected abstract ArgumentType<?> makeArgumentType();

    protected abstract Object makeArgumentObject(CommandContext<ServerCommandSource> context, Argument argument);

    public Object makeParameterObject(CommandContext<ServerCommandSource> ctx, Argument argument) {
        Object argumentObject = this.makeArgumentObject(ctx, argument);
        return box(argument, argumentObject);
    }

    public boolean verifyCommandSource(CommandContext<ServerCommandSource> context) {
        return true;
    }

    private static Object box(Argument argument, Object value) {
        // pack the type
        if (argument.isOptional()) {
            return Optional.of(value);
        }

        return value;
    }

    public static BaseArgumentTypeAdapter getAdapter(Class<?> type) {
        for (BaseArgumentTypeAdapter adapter : adapters) {
            if (adapter.match(type)) {
                return adapter;
            }
        }

        throw new RuntimeException("No adapters match the argument type: " + type.getTypeName());
    }

}
