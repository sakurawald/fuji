package io.github.sakurawald.module.initializer.command_meta.delay;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.core.event.impl.ServerLifecycleEvents;
import io.github.sakurawald.core.service.command_executor.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DelayInitializer extends ModuleInitializer {

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> executor.shutdown());
    }

    @CommandNode("delay")
    @CommandRequirement(level = 4)
    private static int delay(int time, GreedyString rest) {

        String $rest = rest.getValue();

        executor.schedule(() -> {
            CommandExecutor.executeCommandAsConsole(null, $rest);
        }, time, TimeUnit.SECONDS);

        return CommandHelper.Return.SUCCESS;
    }
}
