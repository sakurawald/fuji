package io.github.sakurawald.core.command.argument.adapter.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.core.command.argument.structure.Argument;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public class EntityAnchorArgumentTypeAdapter extends BaseArgumentTypeAdapter {
    @Override
    protected ArgumentType<?> makeArgumentType() {
        return EntityAnchorArgumentType.entityAnchor();
    }

    @Override
    protected Object makeArgumentObject(CommandContext<ServerCommandSource> context, Argument argument) {
        return EntityAnchorArgumentType.getEntityAnchor(context,argument.getArgumentName());
    }

    @Override
    public List<Class<?>> getTypeClasses() {
        return List.of(EntityAnchorArgumentType.EntityAnchor.class);
    }

    @Override
    public List<String> getTypeStrings() {
        return List.of("entity-anchor");
    }
}