package io.github.sakurawald.core.command.processor;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LanguageHelper;
import io.github.sakurawald.core.auxiliary.minecraft.PermissionHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.core.command.argument.structure.Argument;
import io.github.sakurawald.core.command.exception.SnackException;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.minecraft.server.command.CommandManager.literal;


public class CommandAnnotationProcessor {
    private static final String REQUIRED_ARGUMENT_PLACEHOLDER = "$";
    public static CommandDispatcher<ServerCommandSource> dispatcher;
    public static CommandRegistryAccess registryAccess;

    public static void process() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            /* environment */
            BaseArgumentTypeAdapter.registerAdapters();
            CommandAnnotationProcessor.dispatcher = dispatcher;
            CommandAnnotationProcessor.registryAccess = registryAccess;

            /* scan */
            scanClass();
        }));
    }

    private static void scanClass() {
        Collection<ModuleInitializer> initializers = Managers.getModuleManager().getRegisteredInitializers();

        for (ModuleInitializer initializer : initializers) {
            Class<?> clazz = initializer.getClass();
            processClass(clazz, initializer);
        }
    }

    private static void processClass(Class<?> clazz, Object instance) {
        Set<Method> methods = ReflectionUtil.getMethodsWithAnnotation(clazz, CommandNode.class);
        for (Method method : methods) {
            processMethod(clazz, method, instance);
        }
    }

    private static void processMethod(Class<?> clazz, Method method, Object instance) {
        if (!method.getReturnType().equals(Integer.class)
            && !method.getReturnType().equals(int.class)) {
            throw new RuntimeException("The method `%s` in class `%s` must return Integer.".formatted(method.getName(), clazz.getName()));
        }

        method.setAccessible(true);

        // build
        List<Argument> pattern = makeArgumentList(clazz, method);

        if (pattern.isEmpty()) {
            throw new RuntimeException("The @Command annotation of method `%s` in class `%s` must have at least one argument.".formatted(method.getName(), clazz.getName()));
        }

        LogUtil.debug("register command -> {}", pattern);

        /* first pass */
        List<ArgumentBuilder<ServerCommandSource, ?>> builders = makeArgumentBuilders(pattern, method);
        com.mojang.brigadier.Command<ServerCommandSource> function = makeCommandFunction(method, instance);

        // set requirement (class)
        if (clazz.isAnnotationPresent(CommandRequirement.class)) {
            setRequirement(builders.getFirst(), clazz.getAnnotation(CommandRequirement.class));
        }

        LiteralArgumentBuilder<ServerCommandSource> root = makeRootArgumentBuilder(builders, (last) -> last.executes(function));
        dispatcher.register(root);

        /* second pass */
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
        if (classAnnotation != null) {
            ret.add(new Argument(classAnnotation.value().trim()));
        }

        CommandNode methodAnnotation = method.getAnnotation(CommandNode.class);
        String[] split = methodAnnotation.value().trim().split(" ");
        for (String s : split) {
            ret.add(new Argument(s));
        }

        // auto complete the required arguments
        if (ret.stream().filter(p -> p.getArgumentName().startsWith(REQUIRED_ARGUMENT_PLACEHOLDER)).findAny().isEmpty()) {
            Parameter[] parameters = method.getParameters();
            for (int index = 0; index < parameters.length; index++) {
                Parameter parameter = parameters[index];
                if (parameter.isAnnotationPresent(CommandSource.class)) continue;

                ret.add(new Argument(parameter.getName(), index, parameter.getType().equals(Optional.class)));
            }
        }

        return ret;
    }

    private static boolean validateCommandSource(CommandContext<ServerCommandSource> ctx, Method method) {
        Parameter expectedCommandSourceParameter = null;

        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(CommandSource.class)) {
                expectedCommandSourceParameter = parameter;
                break;
            }
        }

        if (expectedCommandSourceParameter == null) return true;

        return BaseArgumentTypeAdapter.getAdapter(expectedCommandSourceParameter).validateCommandSource(ctx);
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
                // don't swallow the exception.
                Throwable theRealException = e.getCause();

                if (theRealException instanceof SnackException snakeException) {
                    // report it
                    if (snakeException.getMessage() != null) {
                        ctx.getSource().sendMessage(LanguageHelper.getTextByValue(ctx.getSource(), "<red>--- Fuji Snake Exception ---\nClass: %s\nMethod: %s\nMessage: %s", instance.getClass().getName(), method.getName(), snakeException.getMessage()));
                    }

                    // swallow it
                    return CommandHelper.Return.FAIL;
                }

                sendCommandExceptionToSource(ctx.getSource(), theRealException);
                return CommandHelper.Return.FAIL;
            }

            return (int) invoke;
        };
    }

    private static void sendCommandExceptionToSource(ServerCommandSource source, Throwable exception) {
        MutableText mutableText = Text.literal("[fuji exception handler] " + exception.toString());
        source.sendError(Text.translatable("command.failed").styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, mutableText))));
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
        return literal(name);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(Parameter parameter) {
        CommandSource commandSource = parameter.getDeclaredAnnotation(CommandSource.class);
        if (commandSource != null) {
            throw new RuntimeException("It's like you specify a wrong `parameter index` for the command pattern.");
        }

        return BaseArgumentTypeAdapter.getAdapter(parameter).makeRequiredArgumentBuilder(parameter);
    }

    private static void registerOptionalArguments(List<Argument> arguments, Method method) {
        List<String> path = Argument.ofLowestNonOptionalNodePath(arguments);
        com.mojang.brigadier.tree.CommandNode<ServerCommandSource> root = dispatcher.findNode(path);
        com.mojang.brigadier.Command<ServerCommandSource> function = root.getCommand();

        // filter
        arguments.stream().filter(Argument::isOptional).forEach(optionalArgument -> {
            int index = optionalArgument.getMethodParameterIndex();
            Parameter parameter = method.getParameters()[index];

            ArgumentBuilder<ServerCommandSource, ?> optionalArgumentBuilder = literal("--" + optionalArgument.getArgumentName())
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
