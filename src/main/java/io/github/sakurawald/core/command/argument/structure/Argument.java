package io.github.sakurawald.core.command.argument.structure;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 1. Treat RequiredArgument the same as the LiteralArgument, except the GreedyStringArgument.
 * 2. The GreedyStringArgument should always be the last argument.
 */
@Data
public class Argument {
    private static final int THE_METHOD_PARAMETER_INDEX_FOR_LITERAL_ARGUMENT = -1;
    String argumentName;
    int methodParameterIndex;
    boolean isOptional;

    public Argument(String argumentName, int methodParameterIndex, boolean isOptional) {
        this.argumentName = argumentName;
        this.methodParameterIndex = methodParameterIndex;
        this.isOptional = isOptional;
    }

    public Argument(String argumentName, int methodParameterIndex) {
        this(argumentName, methodParameterIndex, false);
    }

    public Argument(String argumentName) {
        this(argumentName, THE_METHOD_PARAMETER_INDEX_FOR_LITERAL_ARGUMENT);
    }

    public boolean isRequiredArgument() {
        // A literal argument doesn't need to get the value from the parameter in the method.
        // A required argument needs to get the value from the parameter in the method, so the index >= 0.
        return this.methodParameterIndex >= 0;
    }

    @Override
    public String toString() {
        if (this.isRequiredArgument()) {
            if (isOptional) {
                return "[$%d = %s]".formatted(this.methodParameterIndex, this.argumentName);
            } else {
                return "<$%d = %s>".formatted(this.methodParameterIndex, this.argumentName);
            }
        }

        return this.argumentName;
    }

    public static List<String> getCommandPathUntilOptionalArgumentNode(List<Argument> arguments) {
        List<String> path = new ArrayList<>();
        for (Argument argument : arguments) {
            if (argument.isOptional) break;
            path.add(argument.getArgumentName());
        }

        return path;
    }

}
