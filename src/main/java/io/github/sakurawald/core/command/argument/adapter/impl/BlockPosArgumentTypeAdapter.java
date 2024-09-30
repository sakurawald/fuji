package io.github.sakurawald.core.command.argument.adapter.impl;


import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.core.command.argument.structure.Argument;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Type;

public class BlockPosArgumentTypeAdapter extends BaseArgumentTypeAdapter {

    @Override
    public boolean match(Type type) {
        return BlockPos.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return BlockPosArgumentType.blockPos();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Argument argument) {
        return BlockPosArgumentType.getBlockPos(context, argument.getArgumentName());
    }
}
