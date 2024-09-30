package io.github.sakurawald.core.command.processor;

import com.mojang.brigadier.CommandDispatcher;
import io.github.sakurawald.core.annotation.Cite;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.core.command.argument.structure.Argument;
import io.github.sakurawald.core.command.structure.CommandDescriptor;
import io.github.sakurawald.core.event.impl.CommandEvents;
import io.github.sakurawald.core.manager.Managers;
import lombok.Getter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Cite({
    "https://github.com/Revxrsal/Lamp"
    , "https://github.com/henkelmax/admiral"
})
public class CommandAnnotationProcessor {

    @Getter
    private static CommandDispatcher<ServerCommandSource> dispatcher;
    @Getter
    private static CommandRegistryAccess registryAccess;

    public static void process() {
        CommandEvents.REGISTRATION.register(((dispatcher, registryAccess, environment) -> {
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
        Managers.getModuleManager().getModuleRegistry().values()
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
        /* verify */
        if (!method.getReturnType().equals(int.class)) {
            throw new RuntimeException("The method `%s` in class `%s` must return the primitive int data type.".formatted(method.getName(), clazz.getName()));
        }

        if (!Modifier.isStatic(method.getModifiers())) {
            throw new RuntimeException("The method `%s` in class `%s` must be static.".formatted(method.getName(), clazz.getName()));
        }

        /* make command descriptor */
        CommandDescriptor descriptor = makeCommandDescriptor(clazz, instance, method);

        /* register the command descriptor */
        descriptor.register();
    }

    private static @NotNull CommandDescriptor makeCommandDescriptor(Class<?> clazz, Object instance, Method method) {
        List<Argument> argumentList = new ArrayList<>();

        /* process the @CommandNode above the class. */
        CommandNode classAnnotation = clazz.getAnnotation(CommandNode.class);
        CommandRequirement classRequirement = clazz.getAnnotation(CommandRequirement.class);
        if (classAnnotation != null && !classAnnotation.value().isBlank()) {
            argumentList.add(Argument.makeLiteralArgument(classAnnotation.value().trim(), classRequirement));
        }

        /* process the @CommandNode above the method. */
        method.setAccessible(true);
        CommandNode methodAnnotation = method.getAnnotation(CommandNode.class);
        CommandRequirement methodRequirement = null;
        for (String argumentName : Arrays.stream(methodAnnotation.value().trim().split(" "))
            .filter(node -> !node.isBlank())
            .toList()) {

            /* pass the class requirement down, if the method requirement is null */
            methodRequirement = method.getAnnotation(CommandRequirement.class);
            if (methodRequirement == null) {
                methodRequirement = clazz.getAnnotation(CommandRequirement.class);
            }

            argumentList.add(Argument.makeLiteralArgument(argumentName, methodRequirement));
        }

        /* process the required arguments */
        boolean hasAnyRequiredArgumentPlaceholder = argumentList.stream().anyMatch(Argument::isRequiredArgumentPlaceholder);
        if (hasAnyRequiredArgumentPlaceholder) {
            /* specify the mappings between argument and parameter manually.  */
            for (int argumentIndex = 0; argumentIndex < argumentList.size(); argumentIndex++) {
                /* find $1, $2 ... and replace them with the correct argument. */
                Argument argument = argumentList.get(argumentIndex);
                if (!argument.isRequiredArgumentPlaceholder()) continue;

                /* replace the required argument placeholder `$1` with the parameter in method whose index is 1*/
                int methodParameterIndex = argument.getMethodParameterIndex();
                Parameter parameter = method.getParameters()[methodParameterIndex];
                boolean isOptional = parameter.getType().equals(Optional.class);
                argumentList.set(argumentIndex, Argument.makeRequiredArgument(parameter.getName(), methodParameterIndex, isOptional, methodRequirement));
            }
        } else {
            /* generate the mappings between argument and parameter automatically. */
            Parameter[] parameters = method.getParameters();
            for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
                Parameter parameter = parameters[parameterIndex];

                /* ignore @CommandSource, since it won't provide value for command argument. */
                if (parameter.isAnnotationPresent(CommandSource.class)) continue;

                /* append the argument to the tail*/
                boolean isOptional = parameter.getType().equals(Optional.class);
                argumentList.add(Argument.makeRequiredArgument(parameter.getName(), parameterIndex, isOptional, methodRequirement));
            }
        }

        /* verify */
        if (argumentList.isEmpty()) {
            throw new RuntimeException("The argument list of @CommandNode annotated in method `%s` in class `%s` is empty.".formatted(method.getName(), clazz.getName()));
        }

        return new CommandDescriptor(instance, method, argumentList);
    }

}
