package io.github.sakurawald.command.processor;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.adapter.ArgumentTypeAdapter;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.ReflectionUtil;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.PermissionHelper;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;


public class BrigadierAnnotationProcessor {
    private static final String REQUIRED_ARGUMENT_PLACEHOLDER = "$";
    private static CommandDispatcher<ServerCommandSource> dispatcher;

    public static void register() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            ArgumentTypeAdapter.registerAdapters();

            BrigadierAnnotationProcessor.dispatcher = dispatcher;
            process();
        }));
    }

    private static void process() {
        Collection<ModuleInitializer> initializers = Managers.getModuleManager().getInitializers();

        for (ModuleInitializer initializer : initializers) {
            Class<?> clazz = initializer.getClass();
            processClass(clazz, initializer);
        }
    }

    private static void processClass(Class<?> clazz, Object instance) {
        Set<Method> methods = ReflectionUtil.getMethodsWithAnnotation(clazz, Command.class);
        for (Method method : methods) {
            processMethod(clazz, method, instance);
        }
    }

    private static void processMethod(Class<?> clazz, Method method, Object instance) {
        method.setAccessible(true);

        // build
        List<String> pattern = makeArgumentPattern(clazz, method);

        LogUtil.warn("register command pattern = {}", pattern);

        List<ArgumentBuilder<ServerCommandSource, ?>> builders = makeArgumentBuilders(pattern, method);
        com.mojang.brigadier.Command<ServerCommandSource> function = makeCommandFunction(method, instance);

        // set requirement (override requirement)
        builders.forEach(builder -> setRequirement(builder, clazz.getAnnotation(CommandPermission.class)));

        LiteralArgumentBuilder<ServerCommandSource> root = makeRootArgumentBuilder(builders, (last) -> last.executes(function));

        // register
        dispatcher.register(root);
    }

    private static void setRequirement(ArgumentBuilder<ServerCommandSource, ?> builder, CommandPermission annotation) {
        if (annotation == null) return;

        if (annotation.level() != 0) {
            builder.requires(ctx -> ctx.hasPermissionLevel(annotation.level()));
            return;
        }

        if (!annotation.permission().isEmpty()) {
            builder.requires(ctx -> {
                ServerPlayerEntity player = ctx.getPlayer();
                if (player == null) return true;
                return PermissionHelper.hasPermission(player, annotation.permission());
            });
            return;
        }
    }



    private static List<String> makeArgumentPattern(Class<?> clazz, Method method) {
        List<String> ret = new ArrayList<>();

        Command classAnnotation = clazz.getAnnotation(Command.class);
        if (classAnnotation != null) {
            ret.add(classAnnotation.value().trim());
        }

        Command methodAnnotation = method.getAnnotation(Command.class);
        String[] split = methodAnnotation.value().trim().split(" ");
        ret.addAll(Arrays.stream(split).toList());

        // auto complete the required arguments
        if (ret.stream().filter(p -> p.startsWith(REQUIRED_ARGUMENT_PLACEHOLDER)).findAny().isEmpty()) {
            Parameter[] parameters = method.getParameters();
            for (int index = 0; index < parameters.length; index++) {
                Parameter parameter = parameters[index];
                if (parameter.isAnnotationPresent(CommandSource.class)) continue;
                ret.add(REQUIRED_ARGUMENT_PLACEHOLDER + index);
            }
        }

        return ret;
    }

    private static boolean validateCommandSource(CommandContext<ServerCommandSource> ctx, Method method) {
        Type expectedCommandSourceType = null;

        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(CommandSource.class)) {
                expectedCommandSourceType = parameter.getType();
                break;
            }
        }

        if (expectedCommandSourceType == null) return true;

        return ArgumentTypeAdapter.getAdapter(expectedCommandSourceType).validateCommandSource(ctx);
    }

    private static com.mojang.brigadier.Command<ServerCommandSource> makeCommandFunction(Method method, Object instance) {
        return (ctx) -> {
            // validate command source
            if (!validateCommandSource(ctx, method)) {
                return CommandHelper.Return.FAIL;
            }

            List<Object> args = makeCommandFunctionArgs(ctx, method);
            Object invoke;
            try {
                invoke = method.invoke(instance, args.toArray());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return (int) invoke;
        };
    }

    private static List<Object> makeCommandFunctionArgs(CommandContext<ServerCommandSource> ctx, Method method) {
        List<Object> args = new ArrayList<>();

        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            Object arg = ArgumentTypeAdapter.getAdapter(parameter.getType()).makeArgumentObject(ctx, parameter);
            args.add(arg);
        }

        return args;
    }

    private static LiteralArgumentBuilder<ServerCommandSource> makeLiteralArgumentBuilder(String name) {
        return CommandManager.literal(name);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(Parameter parameter) {
        CommandSource commandSource = parameter.getDeclaredAnnotation(CommandSource.class);
        if (commandSource != null) {
            throw new RuntimeException("It's like you specify a wrong `parameter index` for the command pattern.");
        }

        return ArgumentTypeAdapter.getAdapter(parameter.getType()).makeRequiredArgumentBuilder(parameter);
    }

    private static List<ArgumentBuilder<ServerCommandSource, ?>> makeArgumentBuilders(List<String> pattern, Method method) {
        List<ArgumentBuilder<ServerCommandSource, ?>> builders = new ArrayList<>();

        for (String name : pattern) {
            ArgumentBuilder<ServerCommandSource, ?> builder;
            if (name.startsWith("$")) {
                int index = Integer.parseInt(name.substring(1));
                Parameter parameter = method.getParameters()[index];
                builder = makeRequiredArgumentBuilder(parameter);
            } else {
                builder = makeLiteralArgumentBuilder(name);
            }

            // set requirement
            setRequirement(builder, method.getAnnotation(CommandPermission.class));

            // add argument node
            builders.add(builder);
        }

        return builders;
    }

    @SuppressWarnings("unchecked")
    private static LiteralArgumentBuilder<ServerCommandSource> makeRootArgumentBuilder(List<ArgumentBuilder<ServerCommandSource, ?>> builders, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> function) {

        ArgumentBuilder<ServerCommandSource, ?> root = null;

        for (int i = builders.size() - 1; i >= 0; i--) {
            ArgumentBuilder<ServerCommandSource, ?> node = builders.get(i);

            if (root == null) {
                root = node;
                root = function.apply(root);
                continue;
            }

            root = node.then(root);
        }

        // the command dispatcher only accepts the LiteralArgumentBuilder as the root command node.
        return (LiteralArgumentBuilder<ServerCommandSource>) root;
    }

}
