package io.github.sakurawald.module.initializer.afk;

import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.afk.accessor.AfkStateAccessor;
import io.github.sakurawald.module.initializer.afk.job.AfkMarkerJob;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.network.ServerPlayerEntity;


public class AfkInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> new AfkMarkerJob().schedule());
    }

    @CommandNode("afk")
    private int $afk(@CommandSource ServerPlayerEntity player) {
        // note: issue command will update lastLastActionTime, so it's impossible to use /afk to disable afk
        ((AfkStateAccessor) player).fuji$setAfk(true);
        MessageHelper.sendMessage(player, "afk.on");
        return CommandHelper.Return.SUCCESS;
    }

}
