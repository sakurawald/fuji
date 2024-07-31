package io.github.sakurawald.module.initializer.command_scheduler.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.adapter.ArgumentTypeAdapter;
import io.github.sakurawald.module.initializer.command_scheduler.CommandSchedulerInitializer;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class ScheduleJobArgumentTypeAdapter extends ArgumentTypeAdapter {

    @Override
    public boolean match(Type type) {
        return ScheduleJobName.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return StringArgumentType.string();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Parameter parameter) {
        return new ScheduleJobName(StringArgumentType.getString(context, parameter.getName()));
    }

    @Override
    public RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(Parameter parameter) {
        return super.makeRequiredArgumentBuilder(parameter).suggests((context, builder) -> {
            CommandSchedulerInitializer.getSchedulerHandler().model().scheduleJobs.forEach(job -> builder.suggest(job.getName()));
            return builder.buildFuture();
        });
    }
}
