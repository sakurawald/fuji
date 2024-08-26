package io.github.sakurawald.module.initializer.command_meta.delay;

import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.module.common.service.command_executor.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

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
    @CommandPermission(level = 4)
    private int delay(int time, GreedyString rest) {

        String $rest = rest.getString();

        executor.schedule(() -> {
            CommandExecutor.executeCommandAsConsole(null, $rest);
        }, time, TimeUnit.SECONDS);

        return CommandHelper.Return.SUCCESS;
    }
}
