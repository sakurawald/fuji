package io.github.sakurawald.module.initializer.command_toolbox.send_broadcast;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;
import io.github.sakurawald.util.PlayerUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SendBroadcastInitializer extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("sendbroadcast")
                        .then(argument("message", StringArgumentType.greedyString())
                                .executes((ctx) -> {
                                    String message = StringArgumentType.getString(ctx, "message");

                                    for (ServerPlayerEntity player : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
                                        player.sendMessage(MessageUtil.ofText(player, false, message));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
        );
    }

}
