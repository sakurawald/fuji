package io.github.sakurawald.command.processor;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.ReflectionUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static net.minecraft.server.command.CommandManager.literal;


public class BrigadierAnnotationProcessor {
    private static CommandDispatcher<ServerCommandSource> dispatcher;

    public static void register() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            BrigadierAnnotationProcessor.dispatcher = dispatcher;
            process();
        }));
    }

    private static void processClass(Class<?> clazz) {
        Set<Method> methods = ReflectionUtil.getMethodsWithAnnotation(clazz, Command.class);
        for (Method method : methods) {
            processMethod(clazz, method);
        }
    }


    private static List<Object> makeCommandArgs(CommandContext<ServerCommandSource> ctx, Method method) {
        List<Object> args = new ArrayList<>();

        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {

            LogUtil.warn("parameter type is {}", parameter.getType().getSimpleName());

            Object arg = null;
            if (parameter.getType().equals(CommandContext.class)) {
                arg = ctx;
            } else if (parameter.getType().equals(ServerPlayerEntity.class)) {
                arg = ctx.getSource().getPlayer();
            }

            args.add(arg);
        }

        return args;
    }


    private static List<String> makeCommandNodePath(Class<?> clazz, Method method) {

        List<String> ret = new ArrayList<>();
        Command rootCommand = clazz.getAnnotation(Command.class);
        String name = rootCommand.value();
        if (!name.isEmpty()) {
            ret.add(rootCommand.value());
        }

        Command annotation = method.getAnnotation(Command.class);

        String[] split = annotation.value().split(" ");

        ret.addAll(Arrays.stream(split).toList());

        return ret;
    }

    private static com.mojang.brigadier.Command<ServerCommandSource> makeCommandExecuteClosure(Method method) {
        return (ctx) -> {
            List<Object> args = makeCommandArgs(ctx, method);
            Object invoke;
            try {
                invoke = method.invoke(null, args.toArray());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return (int) invoke;
        };
    }

    private static void processMethod(Class<?> clazz, Method method) {
        method.setAccessible(true);

        // build
        List<String> literalNodes = makeCommandNodePath(clazz, method);

        LogUtil.warn("literal nodes = {}", literalNodes);

        LiteralArgumentBuilder<ServerCommandSource> root = null;
        for (int i = literalNodes.size() - 1; i >= 0; i--) {
            String name = literalNodes.get(i);

            if (i == literalNodes.size() - 1) {
                root = literal(name).executes(makeCommandExecuteClosure(method));
                continue;
            }

            root = literal(name).then(root);
        }

        // register
        dispatcher.register(root);
    }

    private static void process() {
        Reflections reflections = new Reflections("io.github.sakurawald.module");

        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Command.class)) {
            processClass(clazz);
        }
    }

}
