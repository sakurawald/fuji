package io.github.sakurawald.module.initializer.command_toolbox.send_message;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SendMessageInitializer extends ModuleInitializer {
    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("sendmessage")
                        .requires(ctx -> ctx.hasPermissionLevel(4))
                        .then(CommandHelper.Argument.player()
                                .then(CommandHelper.Argument.rest()
                                        .executes((ctx) -> {
                                            ServerPlayerEntity player = CommandHelper.Argument.player(ctx);
                                            String message = CommandHelper.Argument.rest(ctx);

                                            player.sendMessage(MessageHelper.ofText(player, false,message));
                                            return CommandHelper.Return.SUCCESS;
                                        })
                                )
                        )
        );
    }

}
