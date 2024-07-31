package io.github.sakurawald.module.initializer.kit.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.adapter.ArgumentTypeAdapter;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.kit.KitInitializer;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class KitNameArgumentTypeAdapter extends ArgumentTypeAdapter {

    private static final KitInitializer initializer = Managers.getModuleManager().getInitializer(KitInitializer.class);

    @Override
    public boolean match(Type type) {
        return KitName.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return StringArgumentType.string();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        return new KitName(StringArgumentType.getString(context, parameter.getName()));
    }

    @Override
    public RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(Parameter parameter) {
        return super.makeRequiredArgumentBuilder(parameter).suggests((context, builder) -> {
            initializer.getKitNameList().forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
