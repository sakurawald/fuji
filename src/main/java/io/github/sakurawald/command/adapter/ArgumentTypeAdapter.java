package io.github.sakurawald.command.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class ArgumentTypeAdapter {

    private static final List<ArgumentTypeAdapter> adapters = new ArrayList<>();

    public static void registerAdapters() {
        Reflections reflections = new Reflections(ArgumentTypeAdapter.class.getPackage().getName());
        reflections.getSubTypesOf(ArgumentTypeAdapter.class).forEach(o -> {
            try {
                Constructor<? extends ArgumentTypeAdapter> constructor = o.getDeclaredConstructor();
                ArgumentTypeAdapter instance = constructor.newInstance();
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

    public static ArgumentTypeAdapter getAdapter(Type type) {
        for (ArgumentTypeAdapter adapter : adapters) {
            if (adapter.match(type)) {
                return adapter;
            }
        }

        throw new RuntimeException("Unsupported argument type " + type.getTypeName());
    }

}
