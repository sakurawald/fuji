package io.github.sakurawald.module.initializer.command_toolbox.ping;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;


public class PingInitializer extends ModuleInitializer {

    @CommandNode("ping")
    private int $ping(@CommandSource CommandContext<ServerCommandSource> ctx, ServerPlayerEntity target) {
        String name = target.getGameProfile().getName();

        int latency = target.networkHandler.getLatency();
        LocaleHelper.sendMessageByKey(ctx.getSource(), "ping.player", name, latency);

        return CommandHelper.Return.SUCCESS;
    }

}
