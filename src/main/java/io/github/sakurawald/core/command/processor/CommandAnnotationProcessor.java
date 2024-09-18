package io.github.sakurawald.core.command.processor;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.PermissionHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.core.command.argument.structure.Argument;
import io.github.sakurawald.core.command.exception.AbortOperationException;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.manager.impl.module.ModuleManager;
import lombok.Getter;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandAnnotationProcessor {

    private static final String REQUIRED_ARGUMENT_PLACEHOLDER = "$";

    @Getter
    private static CommandDispatcher<ServerCommandSource> dispatcher;
    @Getter
    private static CommandRegistryAccess registryAccess;

    public static void process() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            /* environment */
            CommandAnnotationProcessor.dispatcher = dispatcher;
            CommandAnnotationProcessor.registryAccess = registryAccess;

            /* register argument type adapters */
            BaseArgumentTypeAdapter.registerAdapters();

            /* register commands */
            processClasses();
        }));
    }

    private static void processClasses() {
        Managers.getModuleManager().getRegisteredInitializers()
            .stream()
            .filter(Objects::nonNull)
            .forEach(initializer -> processClass(initializer.getClass(), initializer));
    }

    private static void processClass(Class<?> clazz, Object instance) {
        Set<Method> methods = ReflectionUtil.getMethodsWithAnnotation(clazz, CommandNode.class);
        for (Method method : methods) {
            processMethod(clazz, instance, method);
        }
    }

    private static void processMethod(Class<?> clazz, Object instance, Method method) {
        if (!method.getReturnType().equals(Integer.class)
            && !method.getReturnType().equals(int.class)) {
            throw new RuntimeException("The method `%s` in class `%s` must return Integer.".formatted(method.getName(), clazz.getName()));
        }

        // ignore visibility
        method.setAccessible(true);

        // make argument list
        List<Argument> pattern = makeArgumentList(clazz, method);
        if (pattern.isEmpty()) {
            throw new RuntimeException("The @CommandNode annotation of method `%s` in class `%s` must have at least one argument.".formatted(method.getName(), clazz.getName()));
        }
        LogUtil.debug("register command: /{}", pattern.stream().map(Argument::toString).collect(Collectors.joining(" ")));

        /* first pass -> make non-optional arguments (literal + required) */
        List<ArgumentBuilder<ServerCommandSource, ?>> builders = makeArgumentBuilders(pattern, method);
        com.mojang.brigadier.Command<ServerCommandSource> function = makeCommandFunction(instance, method);

        /* set requirement (class) */
        if (clazz.isAnnotationPresent(CommandRequirement.class)) {
            setRequirement(builders.getFirst(), clazz.getAnnotation(CommandRequirement.class));
        }

        /* register it */
        LiteralArgumentBuilder<ServerCommandSource> root = makeRootArgumentBuilder(builders, (last) -> last.executes(function));
        dispatcher.register(root);

        /* second pass -> make optional arguments */
        registerOptionalArguments(pattern, method);
    }

    @SuppressWarnings("RedundantIfStatement")
    private static void setRequirement(ArgumentBuilder<ServerCommandSource, ?> builder, CommandRequirement annotation) {
        if (annotation == null) return;

        Predicate<ServerCommandSource> predicate = (ctx) -> {
            ServerPlayerEntity player = ctx.getPlayer();
            if (player == null) return true;
            if (ctx.hasPermissionLevel(annotation.level())) return true;
            if (!annotation.string().isEmpty() && PermissionHelper.hasPermission(player.getUuid(), annotation.string()))
                return true;

            return false;
        };

        builder.requires(predicate);
    }


    private static List<Argument> makeArgumentList(Class<?> clazz, Method method) {
        List<Argument> ret = new ArrayList<>();

        CommandNode classAnnotation = clazz.getAnnotation(CommandNode.class);
        if (classAnnotation != null && !classAnnotation.value().isBlank()) {
            ret.add(new Argument(classAnnotation.value().trim()));
        }

        CommandNode methodAnnotation = method.getAnnotation(CommandNode.class);
        Arrays.stream(methodAnnotation.value().trim().split(" "))
            .filter(node -> !node.isBlank())
            .forEach(node -> ret.add(new Argument(node)));

        boolean isParameterIndexSpecifiedManually = ret.stream().anyMatch(p -> p.getArgumentName().startsWith(REQUIRED_ARGUMENT_PLACEHOLDER));
        if (isParameterIndexSpecifiedManually) {
            /* fill the command pattern manually. */
            for (int i = 0; i < ret.size(); i++) {
                // find $1, $2 ... and replace them with the correct argument.
                Argument argument = ret.get(i);
                if (!argument.getArgumentName().startsWith(REQUIRED_ARGUMENT_PLACEHOLDER)) continue;

                int methodParameterIndex = Integer.parseInt(argument.getArgumentName().substring(REQUIRED_ARGUMENT_PLACEHOLDER.length()));
                Parameter parameter = method.getParameters()[methodParameterIndex];
                // e.g. replace $1 with the parameter in method whose index is 1.
                ret.set(i, new Argument(parameter.getName(), methodParameterIndex, parameter.getType().equals(Optional.class)));
            }
        } else {
            /* fill the command pattern automatically. */
            Parameter[] parameters = method.getParameters();
            for (int index = 0; index < parameters.length; index++) {
                Parameter parameter = parameters[index];
                // ignore @CommandSource, since it won't provide value for command argument.
                if (parameter.isAnnotationPresent(CommandSource.class)) continue;
                ret.add(new Argument(parameter.getName(), index, parameter.getType().equals(Optional.class)));
            }
        }

        return ret;
    }

    private static boolean verifyCommandSource(CommandContext<ServerCommandSource> ctx, Method method) {
        Parameter expectedCommandSourceParameter = null;

        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(CommandSource.class)) {
                expectedCommandSourceParameter = parameter;
                break;
            }
        }

        if (expectedCommandSourceParameter == null) return true;

        return BaseArgumentTypeAdapter.getAdapter(expectedCommandSourceParameter).verifyCommandSource(ctx);
    }

    private static com.mojang.brigadier.Command<ServerCommandSource> makeCommandFunction(Object instance, Method method) {
        return (ctx) -> {
            // verify command source
            if (!verifyCommandSource(ctx, method)) {
                return CommandHelper.Return.FAIL;
            }

            List<Object> args = makeCommandFunctionArgs(ctx, method);
            Object invoke;
            try {
                invoke = method.invoke(instance, args.toArray());
            } catch (IllegalAccessException | InvocationTargetException e) {
                // don't swallow the exception.
                Throwable theRealException = e.getCause();

                if (theRealException instanceof AbortOperationException snakeException) {
                    // report it
                    if (snakeException.getMessage() != null) {
                        reportException(ctx.getSource(), instance, method, theRealException);
                    }

                    // swallow it
                    return CommandHelper.Return.FAIL;
                }

                reportException(ctx.getSource(), instance, method, theRealException);
                return CommandHelper.Return.FAIL;
            }

            return (int) invoke;
        };
    }


    private static void reportException(ServerCommandSource source, Object instance, Method method, Throwable throwable) {
        // report to console
        String string = """
            [Fuji Exception Catcher]
            - Source: %s
            - Module: %s
            - Class: %s
            - Method: %s
            - Message: %s

            """.formatted(
            source.getName()
            , ModuleManager.computeModulePath(instance.getClass().getName())
            , instance.getClass().getName()
            , method.getName()
            , throwable.toString());
        LogUtil.error(string, throwable);

        // report to command source
        String stacktrace = String.join("\n", LogUtil.getStackTraceAsList(throwable));
        Component report = LocaleHelper.getTextByValue(source, string)
            .asComponent()
            .color(TextColor.color(255, 95, 0))
            .hoverEvent(HoverEvent.showText(Component.text("Click to copy the stacktrace.")))
            .clickEvent(ClickEvent.copyToClipboard(stacktrace));
        source.sendMessage(report);
    }

    private static List<Object> makeCommandFunctionArgs(CommandContext<ServerCommandSource> ctx, Method method) {
        List<Object> args = new ArrayList<>();

        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {

            try {
                Object arg = BaseArgumentTypeAdapter.getAdapter(parameter).makeArgumentObject(ctx, parameter);

                if (parameter.getType().equals(Optional.class)) {
                    arg = Optional.of(arg);
                }

                args.add(arg);
            } catch (Exception e) {
                /*
                 * for command redirect, given 3 optional arguments named x, y and z.
                 * The arguments are defined in order: (x, y, z).
                 * The optional argument must be passed in the order that matches the defined order.
                 * If the command source pass the optional arguments in the order (z, x, y), then thw following exceptions will be thrown:
                 * java.lang.IllegalArgumentException, e.message = No such argument 'x' exists on this command
                 * java.lang.IllegalArgumentException, e.message = No such argument 'y' exists on this command
                 *
                 * In order to continue the command-context passing process, we will temporally ignore the exception, so that the optional argument can be filled properly.
                 *
                 * The magic field "No such argument" is thrown by mojang's brigadier system.
                 * */
                if (e.getMessage() != null && e.getMessage().startsWith("No such argument")) {
                    args.add(Optional.empty());
                    continue;
                }

                // throw other exception for upper-level handler
                throw e;
            }

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

        return BaseArgumentTypeAdapter.getAdapter(parameter).makeRequiredArgumentBuilder(parameter);
    }

    private static void registerOptionalArguments(List<Argument> arguments, Method method) {
        List<String> path = Argument.getCommandPathUntilOptionalArgumentNode(arguments);
        com.mojang.brigadier.tree.CommandNode<ServerCommandSource> root = dispatcher.findNode(path);
        com.mojang.brigadier.Command<ServerCommandSource> function = root.getCommand();

        // filter
        arguments.stream().filter(Argument::isOptional).forEach(optionalArgument -> {
            int parameterIndex = optionalArgument.getMethodParameterIndex();
            Parameter parameter = method.getParameters()[parameterIndex];

            ArgumentBuilder<ServerCommandSource, ?> optionalArgumentBuilder = CommandManager.literal("--" + optionalArgument.getArgumentName())
                .then(makeRequiredArgumentBuilder(parameter).executes(function).redirect(root));

            // register it
            root.addChild(optionalArgumentBuilder.build());
        });
    }

    private static ArgumentBuilder<ServerCommandSource, ?> getLastLiteralArgumentBuilder(List<ArgumentBuilder<ServerCommandSource, ?>> builders) {
        for (int i = builders.size() - 1; i >= 0; i--) {
            ArgumentBuilder<ServerCommandSource, ?> builder = builders.get(i);
            if (builder instanceof LiteralArgumentBuilder) {
                return builder;
            }

        }

        throw new RuntimeException("No last literal argument builder found.");
    }

    private static List<ArgumentBuilder<ServerCommandSource, ?>> makeArgumentBuilders(List<Argument> arguments, Method method) {
        List<ArgumentBuilder<ServerCommandSource, ?>> builders = new ArrayList<>();

        for (Argument argument : arguments) {
            String name = argument.getArgumentName();
            ArgumentBuilder<ServerCommandSource, ?> builder;

            if (argument.isRequiredArgument()) {
                int index = argument.getMethodParameterIndex();
                Parameter parameter = method.getParameters()[index];
                builder = makeRequiredArgumentBuilder(parameter);
            } else {
                builder = makeLiteralArgumentBuilder(name);
            }

            // don't add the builder if it's an optional argument
            if (argument.isOptional()) {
                continue;
            }

            // add argument node
            builders.add(builder);
        }


        // set requirement (method)
        setRequirement(getLastLiteralArgumentBuilder(builders), method.getAnnotation(CommandRequirement.class));

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
