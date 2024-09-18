package io.github.sakurawald.module.initializer.command_toolbox.warp.command.argument.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.command_toolbox.warp.WarpInitializer;
import io.github.sakurawald.module.initializer.command_toolbox.warp.command.argument.wrapper.WarpName;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class WarpNameArgumentTypeAdapter extends BaseArgumentTypeAdapter {
    private static final WarpInitializer initializer = Managers.getModuleManager().getInitializer(WarpInitializer.class);

    @Override
    public boolean match(Type type) {
        return WarpName.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return StringArgumentType.string();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        return new WarpName(StringArgumentType.getString(context, parameter.getName()));
    }

    @Override
    public RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(Parameter parameter) {
        return super.makeRequiredArgumentBuilder(parameter).suggests((ctx, builder) -> {
            initializer.getData().getModel().name2warp.keySet().forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}

