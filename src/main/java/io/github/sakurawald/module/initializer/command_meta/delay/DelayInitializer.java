package io.github.sakurawald.module.initializer.command_meta.delay;

import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.module.common.structure.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
