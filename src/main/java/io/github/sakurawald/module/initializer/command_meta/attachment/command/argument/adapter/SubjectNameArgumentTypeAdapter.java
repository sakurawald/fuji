package io.github.sakurawald.module.initializer.command_meta.attachment.command.argument.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.argument.adapter.interfaces.AbstractArgumentTypeAdapter;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.command_meta.attachment.command.argument.wrapper.SubjectName;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class SubjectNameArgumentTypeAdapter extends AbstractArgumentTypeAdapter {
    @Override
    public boolean match(Type type) {
        return SubjectName.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return StringArgumentType.string();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        return new SubjectName(StringArgumentType.getString(context,parameter.getName()));
    }

    @Override
    public RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(Parameter parameter) {
        return super.makeRequiredArgumentBuilder(parameter).suggests((ctx, builder)-> {
            Managers.getAttachmentManager().listSubjectName().forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
