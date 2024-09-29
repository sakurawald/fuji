package io.github.sakurawald.module.initializer.command_toolbox.freeze;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class FreezeInitializer extends ModuleInitializer {

    @CommandNode("freeze")
    @CommandRequirement(level = 4)
    private static int freeze(@CommandSource CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player, int ticks) {
        player.setFrozenTicks(ticks);

        LocaleHelper.sendMessageByKey(ctx.getSource(), "operation.success");
        return CommandHelper.Return.SUCCESS;
    }
}
