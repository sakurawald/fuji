package io.github.sakurawald.command.processor;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.module.mixin.color.sign.ServerPlayerEntityMixin;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.ReflectionUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.logging.LogWriter;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static net.minecraft.server.command.CommandManager.RegistrationEnvironment;
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
            processMethod(clazz,method);
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


    private static void processMethod(Class<?> clazz, Method method) {

        Command annotation = method.getAnnotation(Command.class);
        String[] literalNode = annotation.value();

        dispatcher.register(literal(literalNode[0]).executes(ctx -> {

            List<Object> args = makeCommandArgs(ctx, method);

            Object invoke;
            try {
                invoke = method.invoke(null, args.toArray());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            return (int) invoke;
        }));



    }

    private static void process() {
        Reflections reflections = new Reflections("io.github.sakurawald.module");

        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Command.class)) {
            processClass(clazz);
        }
    }

}
