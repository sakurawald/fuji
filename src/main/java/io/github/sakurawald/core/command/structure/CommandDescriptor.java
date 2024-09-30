package io.github.sakurawald.core.command.structure;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.PermissionHelper;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.core.command.argument.structure.Argument;
import io.github.sakurawald.core.command.exception.AbortCommandExecutionException;
import io.github.sakurawald.core.command.processor.CommandAnnotationProcessor;
import io.github.sakurawald.core.manager.impl.module.ModuleManager;
import lombok.AllArgsConstructor;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CommandDescriptor {
    Object instance;
    Method method;
    List<Argument> arguments;

    private static LiteralArgumentBuilder<ServerCommandSource> makeLiteralArgumentBuilder(String name) {
        return CommandManager.literal(name);
    }

    private static RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(Parameter parameter) {
        /* verify */
        CommandSource commandSource = parameter.getDeclaredAnnotation(CommandSource.class);
        if (commandSource != null) {
            throw new RuntimeException("It's like you specify a wrong `parameter index` for the command pattern.");
        }

        /* use adapter to make the required argument builder */
        return BaseArgumentTypeAdapter.getAdapter(parameter).makeRequiredArgumentBuilder(parameter);
    }

    @SuppressWarnings("RedundantIfStatement")
    private static void setRequirementForArgumentBuilder(@NotNull ArgumentBuilder<ServerCommandSource, ?> builder, @Nullable CommandRequirement annotation) {
        // don't override the command requirement if the annotation is null
        if (annotation == null) return;

        /* make the predicate */
        Predicate<ServerCommandSource> predicate = (ctx) -> {
            ServerPlayerEntity player = ctx.getPlayer();
            if (player == null) return true;
            if (!annotation.string().isEmpty() && PermissionHelper.hasPermission(player.getUuid(), annotation.string()))
                return true;
            if (ctx.hasPermissionLevel(annotation.level())) return true;

            return false;
        };

        /* set the predicate */
        builder.requires(predicate);
    }

    @SuppressWarnings("unchecked")
    private static LiteralArgumentBuilder<ServerCommandSource> makeRootArgumentBuilder(List<ArgumentBuilder<ServerCommandSource, ?>> builders, Command<ServerCommandSource> command) {
        ArgumentBuilder<ServerCommandSource, ?> root = null;

        for (ArgumentBuilder<ServerCommandSource, ?> node : builders.reversed()) {
            if (root == null) {
                root = node;
                root = root.executes(command);
                continue;
            }
            root = node.then(root);
        }

        // the command dispatcher only accepts the LiteralArgumentBuilder for register()
        return (LiteralArgumentBuilder<ServerCommandSource>) root;
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

    private static CommandNode<ServerCommandSource> computeRedirectTargetOfOptionalArgument(List<Argument> arguments) {
        List<String> prefix = arguments.stream()
            .takeWhile(arg -> !arg.isOptional())
            .map(Argument::getArgumentName)
            .toList();

        return CommandAnnotationProcessor.getDispatcher().findNode(prefix);
    }

    private static List<ArgumentBuilder<ServerCommandSource, ?>> makeArgumentBuilders(CommandDescriptor descriptor) {
        List<ArgumentBuilder<ServerCommandSource, ?>> builders = new ArrayList<>();
        descriptor.arguments
            .stream()
            // ignore the optional arguments, since we will process them in the second pass.
            .filter(it -> !it.isOptional())
            .forEach(argument -> {
                // make the builder
                ArgumentBuilder<ServerCommandSource, ?> builder = makeArgumentBuilder(descriptor, argument);

                // set requirement specified by the argument for the builder
                setRequirementForArgumentBuilder(builder, argument.getRequirement());

                // add the builder
                builders.add(builder);
            });

        return builders;
    }

    private static ArgumentBuilder<ServerCommandSource, ?> makeArgumentBuilder(CommandDescriptor descriptor, Argument argument) {
        ArgumentBuilder<ServerCommandSource, ?> builder;
        if (argument.isRequiredArgument()) {
            int parameterIndex = argument.getMethodParameterIndex();
            Parameter parameter = descriptor.method.getParameters()[parameterIndex];
            builder = makeRequiredArgumentBuilder(parameter);
        } else {
            builder = makeLiteralArgumentBuilder(argument.getArgumentName());
        }
        return builder;
    }

    private static Command<ServerCommandSource> makeCommandFunctionClosure(CommandDescriptor descriptor) {
        return (ctx) -> {
            /* verify command source */
            if (!verifyCommandSource(ctx, descriptor.method)) {
                return CommandHelper.Return.FAIL;
            }

            /* invoke the command function */
            List<Object> args = makeCommandFunctionArgs(ctx, descriptor.method);
            Object value;
            try {
                value = descriptor.method.invoke(descriptor.instance, args.toArray());
            } catch (IllegalAccessException | InvocationTargetException e) {
                // get the real exception during reflection.
                Throwable theRealException = e.getCause();

                if (theRealException instanceof AbortCommandExecutionException) {
                    // the logging is done before throwing the AbortOperationException, here we just swallow this exception.
                    return CommandHelper.Return.FAIL;
                }

                reportException(ctx.getSource(), descriptor.instance, descriptor.method, theRealException);
                return CommandHelper.Return.FAIL;
            }

            return (int) value;
        };
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

        MutableText report = LocaleHelper.getTextByValue(source, string)
            .copy()
            .setStyle(Style.EMPTY
                .withColor(16736000)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to copy the stacktrace.")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, stacktrace)));

        source.sendMessage(report);
    }

    public void register() {
        LogUtil.debug("register command: {}", this);

        /* first pass */
        registerNonOptionalArguments();

        /* second pass */
        registerOptionalArguments();
    }

    private void registerNonOptionalArguments() {
        /* make root builder */
        List<ArgumentBuilder<ServerCommandSource, ?>> builders = makeArgumentBuilders(this);
        Command<ServerCommandSource> command = makeCommandFunctionClosure(this);
        LiteralArgumentBuilder<ServerCommandSource> root = makeRootArgumentBuilder(builders, command);

        /* register it */
        CommandAnnotationProcessor.getDispatcher().register(root);
    }

    private void registerOptionalArguments() {
        CommandNode<ServerCommandSource> redirectTargetNode = computeRedirectTargetOfOptionalArgument(this.arguments);

        this.arguments.stream()
            .filter(Argument::isOptional)
            .forEach(optionalArgument -> {
                /* make it */
                int parameterIndex = optionalArgument.getMethodParameterIndex();
                Parameter parameter = this.method.getParameters()[parameterIndex];
                ArgumentBuilder<ServerCommandSource, ?> optionalArgumentBuilder =
                    CommandManager
                        .literal("--" + optionalArgument.getArgumentName())
                        .then(makeRequiredArgumentBuilder(parameter).executes(redirectTargetNode.getCommand()).redirect(redirectTargetNode));

                /* register it */
                redirectTargetNode.addChild(optionalArgumentBuilder.build());
            });
    }

    @Override
    public String toString() {
        return "/" + this.arguments.stream().map(Argument::toString).collect(Collectors.joining(" ")) + "\n"
            + "method = " + this.method;
    }

}
