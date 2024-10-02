package io.github.sakurawald.core.command.argument.structure;

import io.github.sakurawald.core.command.structure.CommandRequirementDescriptor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Rules:
 * - There are 2 kinds of Argument: LiteralArgument and RequiredArgument.
 * - The treatment of RequiredArgument is the same as the LiteralArgument, except the GreedyStringArgument.
 * - The GreedyStringArgument should always be the last parameter written in the method.
 * - An optional argument is a RequiredArgument.
 * - The parameter names in a method annotated with @CommandNode is a part of the command path, be careful to refactor these parameter names.
 */
@Getter
public class Argument {
    private static final String REQUIRED_ARGUMENT_PLACEHOLDER = "$";
    private static final int THE_METHOD_PARAMETER_INDEX_FOR_LITERAL_ARGUMENT = -1;

    final Class<?> type;
    final String argumentName;
    final boolean isOptional;
    final CommandRequirementDescriptor requirement;
    int methodParameterIndex;
    boolean isCommandSource;

    private Argument(@Nullable Class<?> type, @NotNull String argumentName, int methodParameterIndex, boolean isOptional, @Nullable CommandRequirementDescriptor requirement) {
        this.type = type;
        this.argumentName = argumentName;
        this.methodParameterIndex = methodParameterIndex;
        this.isOptional = isOptional;
        this.requirement = requirement;

        // if it's a required argument placeholder...
        this.methodParameterIndex = this.tryParseMethodParameterIndexFromArgumentName();
    }

    public static Argument makeRequiredArgument(@Nullable Class<?> type, @NotNull String argumentName, int methodParameterIndex, boolean isOptional, @Nullable CommandRequirementDescriptor requirement) {
        return new Argument(type, argumentName, methodParameterIndex, isOptional, requirement);
    }

    public static Argument makeLiteralArgument(@NotNull String argumentName, @Nullable CommandRequirementDescriptor requirement) {
        return new Argument(null, argumentName, THE_METHOD_PARAMETER_INDEX_FOR_LITERAL_ARGUMENT, false, requirement);
    }

    public boolean isRequiredArgument() {
        // A literal argument doesn't need to get the value from the parameter in the method.
        // A required argument needs to get the value from the parameter in the method, so the index >= 0.
        return this.methodParameterIndex >= 0;
    }

    public boolean isLiteralArgument() {
        return !this.isRequiredArgument();
    }

    public boolean isRequiredArgumentPlaceholder() {
        return this.argumentName.startsWith(REQUIRED_ARGUMENT_PLACEHOLDER);
    }

    private String computeRequirementString() {
        if (this.requirement != null) {
            return "%d %s".formatted(this.requirement.getLevel(), this.requirement.getString())
                .trim();
        }

        return "";
    }

    @Override
    public String toString() {
        // command source
        String commandSourceString = this.isCommandSource ? "@" : "";

        /* required argument */
        if (this.isRequiredArgument()) {
            if (isOptional) {
                return commandSourceString + "[%s $%d]{%s}".formatted(this.argumentName, this.methodParameterIndex, this.computeRequirementString());
            } else {
                return commandSourceString + "<%s $%d>{%s}".formatted(this.argumentName, this.methodParameterIndex, this.computeRequirementString());
            }
        }

        /* literal argument */
        return "%s{%s}".formatted(this.argumentName, this.computeRequirementString());
    }

    private int tryParseMethodParameterIndexFromArgumentName() {
        // parse the method parameter index
        if (argumentName.startsWith(REQUIRED_ARGUMENT_PLACEHOLDER)) {
            this.methodParameterIndex = Integer.parseInt(argumentName.substring(REQUIRED_ARGUMENT_PLACEHOLDER.length()));
        }

        return methodParameterIndex;
    }

    public Argument markAsCommandSource() {
        if (!this.isRequiredArgument())
            throw new IllegalArgumentException("The argument for command source must be a required argument.");
        if (this.getType() == null)
            throw new IllegalArgumentException("The type of the argument for command source must not null.");

        this.isCommandSource = true;
        return this;
    }

}
