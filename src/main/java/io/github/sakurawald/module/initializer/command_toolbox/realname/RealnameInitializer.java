package io.github.sakurawald.module.initializer.command_toolbox.realname;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;


public class RealnameInitializer extends ModuleInitializer {

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("realname").executes(this::$realname));
    }

    private int $realname(CommandContext<ServerCommandSource> ctx) {
        TextComponent.Builder builder = Component.empty().toBuilder();

        for (ServerPlayerEntity player : Fuji.SERVER.getPlayerManager().getPlayerList()) {
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
        return Command.SINGLE_SUCCESS;
    }

}
