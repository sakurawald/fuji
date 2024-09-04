package io.github.sakurawald.module.initializer.home.command.argument.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.structure.Position;
import io.github.sakurawald.module.initializer.home.HomeInitializer;
import io.github.sakurawald.module.initializer.home.command.argument.wrapper.HomeName;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Map;

@SuppressWarnings("unused")
public class HomeArgumentTypeAdapter extends BaseArgumentTypeAdapter {

    private static final HomeInitializer initializer = Managers.getModuleManager().getInitializer(HomeInitializer.class);

    @Override
    public boolean match(Type type) {
        return HomeName.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return StringArgumentType.string();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        return new HomeName(StringArgumentType.getString(context, parameter.getName()));
    }

    @Override
    public RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(Parameter parameter) {
        return super.makeRequiredArgumentBuilder(parameter).suggests((context, builder) -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player == null) return builder.buildFuture();

                    Map<String, Position> name2position = initializer.ofHomes(player);
                    name2position.keySet().forEach(builder::suggest);
                    return builder.buildFuture();
                }
        );
    }
}
