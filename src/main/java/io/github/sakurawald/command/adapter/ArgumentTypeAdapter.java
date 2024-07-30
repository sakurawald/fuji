package io.github.sakurawald.command.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class ArgumentTypeAdapter {

    private static final List<ArgumentTypeAdapter> adapters = new ArrayList<>();

    Type type;

    public ArgumentTypeAdapter(Type type) {
        this.type = type;
    }

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

    public boolean match(Type type) {
        return this.type.equals(type);
    }

    public abstract ArgumentType<?> makeArgumentType();

    public abstract Object makeArgumentObject(CommandContext<ServerCommandSource> context);

    public abstract boolean validateCommandSource(CommandContext<ServerCommandSource> context);

    public static ArgumentTypeAdapter getAdapter(Type type) {
        for (ArgumentTypeAdapter adapter : adapters) {
            if (adapter.match(type)) {
                return adapter;
            }
        }

        throw new RuntimeException("Unsupported argument type " + type.getTypeName());
    }

}
