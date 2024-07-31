package io.github.sakurawald.module.initializer.command_meta.delay;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.adapter.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.module.common.structure.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DelayInitializer extends ModuleInitializer {
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Command("delay")
    @CommandPermission(level = 4)
    private int delay(int time, GreedyString rest) {

        String $rest = rest.getString();

        executor.schedule(() -> {
            CommandExecutor.executeCommandAsConsole(null, $rest);
        }, time, TimeUnit.SECONDS);

        return CommandHelper.Return.SUCCESS;
    }
}
