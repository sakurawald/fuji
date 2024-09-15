package io.github.sakurawald.core.command.argument.adapter.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import lombok.SneakyThrows;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class PlayerArgumentTypeAdapter extends BaseArgumentTypeAdapter {

    @Override
    public boolean match(Type type) {
        return ServerPlayerEntity.class.equals(type);
    }

    @Override
    public ArgumentType<?> makeArgumentType() {
        return EntityArgumentType.player();
    }

    @SneakyThrows
    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        if (parameter.isAnnotationPresent(CommandSource.class)) {
            return context.getSource().getPlayer();
        }

        return EntityArgumentType.getPlayer(context,parameter.getName());
    }

    @Override
    public boolean verifyCommandSource(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            LocaleHelper.sendMessageByKey(context.getSource(), "command.player_only");
            return false;
        }

        return true;
    }
}
