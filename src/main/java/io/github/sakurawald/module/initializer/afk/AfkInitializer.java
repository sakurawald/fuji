package io.github.sakurawald.module.initializer.afk;

import com.mojang.brigadier.CommandDispatcher;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.afk.job.AfkMarkerJob;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;


public class AfkInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            new AfkMarkerJob().schedule();
        });
    }

    @Command("afk")
    private int $afk(@CommandSource ServerPlayerEntity player) {
        // note: issue command will update lastLastActionTime, so it's impossible to use /afk to disable afk
        ((AfkStateAccessor) player).fuji$setAfk(true);
        MessageHelper.sendMessage(player, "afk.on");
        return CommandHelper.Return.SUCCESS;
    }

}
