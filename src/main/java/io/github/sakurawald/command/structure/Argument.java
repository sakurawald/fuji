package io.github.sakurawald.command.structure;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.Data;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;

@Data
public class Argument {
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
        this(argumentName, -1);
    }

    public boolean isRequiredArgument() {
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

    public static List<String> ofNodePath(List<Argument> arguments) {
        List<String> path = new ArrayList<>();
        for (Argument argument : arguments) {
            path.add(argument.getArgumentName());
        }

        return path;
    }

    public static List<String> ofLowestNonOptionalNodePath(List<Argument> arguments) {
        List<String> path = new ArrayList<>();
        for (Argument argument : arguments) {
            if (argument.isOptional) break;
            path.add(argument.getArgumentName());
        }

        return path;
    }

//    public static LiteralArgumentBuilder<ServerCommandSource, ?> makeRootArgumentBuilder() {
//
//    }

}
