package io.github.sakurawald.module.initializer.command_toolbox.ping;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;


public class PingInitializer extends ModuleInitializer {

    @CommandNode("ping")
    private int $ping(@CommandSource CommandContext<ServerCommandSource> ctx, ServerPlayerEntity target) {
        String name = target.getGameProfile().getName();

        int latency = target.networkHandler.getLatency();
        MessageHelper.sendMessage(ctx.getSource(), "ping.player", name, latency);

        return CommandHelper.Return.SUCCESS;
    }

}
