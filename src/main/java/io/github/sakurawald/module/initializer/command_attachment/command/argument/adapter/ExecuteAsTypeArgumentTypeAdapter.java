package io.github.sakurawald.module.initializer.command_attachment.command.argument.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.core.command.argument.structure.Argument;
import io.github.sakurawald.module.initializer.command_attachment.command.argument.wrapper.ExecuteAsType;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Type;

public class ExecuteAsTypeArgumentTypeAdapter extends BaseArgumentTypeAdapter {
    @Override
    public boolean match(Type type) {
        return ExecuteAsType.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return StringArgumentType.string();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Argument argument) {
        return ExecuteAsType.valueOf(StringArgumentType.getString(context, argument.getArgumentName()));
    }

    @Override
    public RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(String argumentName) {
        return super.makeRequiredArgumentBuilder(argumentName).suggests((ctx, builder) -> {
            for (ExecuteAsType value : ExecuteAsType.values()) {
                builder.suggest(value.name());
            }

            return builder.buildFuture();
        });
    }
}
