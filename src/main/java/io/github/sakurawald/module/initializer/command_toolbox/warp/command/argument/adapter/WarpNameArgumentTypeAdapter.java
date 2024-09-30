package io.github.sakurawald.module.initializer.command_toolbox.warp.command.argument.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.core.command.argument.structure.Argument;
import io.github.sakurawald.module.initializer.command_toolbox.warp.WarpInitializer;
import io.github.sakurawald.module.initializer.command_toolbox.warp.command.argument.wrapper.WarpName;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Type;

public class WarpNameArgumentTypeAdapter extends BaseArgumentTypeAdapter {

    @Override
    public boolean match(Type type) {
        return WarpName.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return StringArgumentType.string();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Argument argument) {
        return new WarpName(StringArgumentType.getString(context, argument.getArgumentName()));
    }

    @Override
    public RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(String argumentName) {
        return super.makeRequiredArgumentBuilder(argumentName).suggests((ctx, builder) -> {
            WarpInitializer.data.getModel().name2warp.keySet().forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}

