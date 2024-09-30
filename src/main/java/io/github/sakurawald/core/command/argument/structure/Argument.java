package io.github.sakurawald.core.command.argument.structure;

import io.github.sakurawald.core.command.annotation.CommandRequirement;
import lombok.Getter;

/**
 * 1. Treat RequiredArgument the same as the LiteralArgument, except the GreedyStringArgument.
 * 2. The GreedyStringArgument should always be the last argument.
 */
@Getter
public class Argument {
    private static final String REQUIRED_ARGUMENT_PLACEHOLDER = "$";
    private static final int THE_METHOD_PARAMETER_INDEX_FOR_LITERAL_ARGUMENT = -1;

    final String argumentName;
    final boolean isOptional;
    final CommandRequirement requirement;
    int methodParameterIndex;

    private Argument(String argumentName, int methodParameterIndex, boolean isOptional, CommandRequirement requirement) {
        this.argumentName = argumentName;
        this.methodParameterIndex = methodParameterIndex;
        this.isOptional = isOptional;
        this.requirement = requirement;

        // parse the method parameter index
        this.methodParameterIndex = this.parseMethodParameterIndex();
    }

    public static Argument makeRequiredArgument(String argumentName, int methodParameterIndex, boolean isOptional, CommandRequirement requirement) {
        return new Argument(argumentName, methodParameterIndex, isOptional, requirement);
    }

    public static Argument makeLiteralArgument(String argumentName, CommandRequirement requirement) {
        return new Argument(argumentName, THE_METHOD_PARAMETER_INDEX_FOR_LITERAL_ARGUMENT, false, requirement);
    }

    public boolean isRequiredArgument() {
        // A literal argument doesn't need to get the value from the parameter in the method.
        // A required argument needs to get the value from the parameter in the method, so the index >= 0.
        return this.methodParameterIndex >= 0;
    }

    public boolean isRequiredArgumentPlaceholder() {
        return this.argumentName.startsWith(REQUIRED_ARGUMENT_PLACEHOLDER);
    }

    private String computeRequirementString() {
        if (this.requirement != null) {
            return "%d %s".formatted(this.requirement.level(), this.requirement.string())
                .trim();
        }

        return "";
    }

    @Override
    public String toString() {
        /* required argument */
        if (this.isRequiredArgument()) {
            if (isOptional) {
                return "[%s $%d]{%s}".formatted(this.argumentName, this.methodParameterIndex, this.computeRequirementString());
            } else {
                return "<%s $%d>{%s}".formatted(this.argumentName, this.methodParameterIndex, this.computeRequirementString());
            }
        }

        /* literal argument */
        return "%s{%s}".formatted(this.argumentName, this.computeRequirementString());
    }

    private int parseMethodParameterIndex() {
        // parse the method parameter index
        if (argumentName.startsWith(REQUIRED_ARGUMENT_PLACEHOLDER)) {
            this.methodParameterIndex = Integer.parseInt(argumentName.substring(REQUIRED_ARGUMENT_PLACEHOLDER.length()));
        }

        return methodParameterIndex;
    }
}
