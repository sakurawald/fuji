package io.github.sakurawald.module.initializer.command_toolbox.more;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import net.minecraft.server.command.ServerCommandSource;


public class MoreInitializer extends ModuleInitializer {

    @Command("more")
    @CommandPermission(level = 4)
    private int $more(@CommandSource CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.itemOnHandCommand(ctx, ((player, itemStack) -> {
            itemStack.setCount(itemStack.getMaxCount());
            return CommandHelper.Return.SUCCESS;
        }));
    }

}
