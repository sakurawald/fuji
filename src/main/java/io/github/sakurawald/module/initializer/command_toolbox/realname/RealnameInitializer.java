package io.github.sakurawald.module.initializer.command_toolbox.realname;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.ServerHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;


public class RealnameInitializer extends ModuleInitializer {


    @Command("realname")
    private int $realname(@CommandSource CommandContext<ServerCommandSource> ctx) {
        TextComponent.Builder builder = Component.empty().toBuilder();

        for (ServerPlayerEntity player : ServerHelper.getDefaultServer().getPlayerManager().getPlayerList()) {
            Text displayName = player.getDisplayName();
            if (displayName == null) continue;

            Component from = displayName.asComponent();
            Component to = player.getName().asComponent();

            builder.append(from)
                    .append(Component.text(" -> "))
                    .append(to)
                    .appendNewline();
        }

        ctx.getSource().sendMessage(builder.build());
        return CommandHelper.Return.SUCCESS;
    }

}
