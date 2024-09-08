package io.github.sakurawald.module.initializer.command_toolbox.burn;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class BurnInitializer extends ModuleInitializer {

    @CommandNode("burn")
    @CommandRequirement(level = 4)
    int burn(@CommandSource CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player, int ticks) {
        player.setFireTicks(ticks);

        MessageHelper.sendMessageByKey(ctx.getSource(), "operation.success");
        return CommandHelper.Return.SUCCESS;
    }
}
