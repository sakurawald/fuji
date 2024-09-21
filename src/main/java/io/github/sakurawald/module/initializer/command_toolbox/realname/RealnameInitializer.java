package io.github.sakurawald.module.initializer.command_toolbox.realname;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;


public class RealnameInitializer extends ModuleInitializer {


    @SuppressWarnings("UnnecessaryLocalVariable")
    @CommandNode("realname")
    private static int $realname(@CommandSource CommandContext<ServerCommandSource> ctx) {
        MutableText builder = Text.empty();

        for (ServerPlayerEntity player : ServerHelper.getDefaultServer().getPlayerManager().getPlayerList()) {
            Text displayName = player.getDisplayName();
            if (displayName == null) continue;

            Text from = displayName;
            Text to = player.getName();

            builder.append(from)
                .append(Text.literal(" -> "))
                .append(to)
                .append(Text.literal("\n"));
        }

        ctx.getSource().sendMessage(builder);
        return CommandHelper.Return.SUCCESS;
    }

}
