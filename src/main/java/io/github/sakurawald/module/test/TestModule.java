package io.github.sakurawald.module.test;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.module.chat.mention.MentionPlayersJob;
import io.github.sakurawald.util.ScheduleUtil;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.level.Level;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;


public class TestModule extends AbstractModule {

    @SneakyThrows
    private static int simulateLag(CommandContext<CommandSourceStack> ctx) {
        Fuji.SERVER.getCommands().getDispatcher().execute("execute in minecraft:overworld run test fake-players", ctx.getSource());
        Fuji.SERVER.getCommands().getDispatcher().execute("execute in minecraft:overworld run time set midnight", ctx.getSource());
        Fuji.SERVER.getCommands().getDispatcher().execute("execute in minecraft:the_nether run test fake-players", ctx.getSource());
        Fuji.SERVER.getCommands().getDispatcher().execute("execute in minecraft:the_end run test fake-players", ctx.getSource());

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings({"ConstantValue", "ReassignedVariable", "PointlessArithmeticExpression", "DataFlowIssue"})
    @SneakyThrows
    private static int fakePlayers(CommandContext<CommandSourceStack> ctx) {
        int amount = 25;
        int startIndex = 0;
        if (ctx.getSource().getLevel().dimension() == Level.OVERWORLD) startIndex = amount * 0;
        if (ctx.getSource().getLevel().dimension() == Level.NETHER) startIndex = amount * 1;
        if (ctx.getSource().getLevel().dimension() == Level.END) startIndex = amount * 2;
        for (int i = 0; i < amount; i++) {
            int distance = i * 100;
            Fuji.SERVER.getCommands().getDispatcher().execute("player %d spawn at %d 96 %d".formatted(startIndex++, distance, distance), ctx.getSource());
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int clearChat(CommandContext<CommandSourceStack> ctx) {
        for (int i = 0; i < 50; i++) {
            ctx.getSource().sendMessage(Component.empty());
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int magic(CommandContext<CommandSourceStack> ctx) {

        try {
            for (JobKey jobKey : ScheduleUtil.getScheduler().getJobKeys(GroupMatcher.groupEquals(MentionPlayersJob.class.getName()))) {
                Fuji.log.error("magic() -> jobKey: {}", jobKey);
            }
        } catch (SchedulerException e) {
            Fuji.log.error(e.getMessage());
        }

        return 1;
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
    }

    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("test").requires(s -> s.hasPermission(4))
                        .then(Commands.literal("fake-players").executes(TestModule::fakePlayers))
                        .then(Commands.literal("simulate-lag").executes(TestModule::simulateLag))
                        .then(Commands.literal("clear-chat").executes(TestModule::clearChat))
                        .then(Commands.literal("magic").executes(TestModule::magic))
        );
    }
}
