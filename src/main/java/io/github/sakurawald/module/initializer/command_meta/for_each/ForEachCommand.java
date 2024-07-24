package io.github.sakurawald.module.initializer.command_meta.for_each;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.sakurawald.module.common.structure.CommandExecuter;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ForEachCommand extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(
                literal("foreach")
                        .requires(ctx -> ctx.hasPermissionLevel(4))
                        .then(argument("rest", StringArgumentType.greedyString())
                                .executes((ctx) -> {
                                    String rest = StringArgumentType.getString(ctx, "rest");
                                    MinecraftServer server = ctx.getSource().getServer();
                                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                        CommandExecuter.executeCommandAsConsole(player, rest);
                                    }
                                    return CommandHelper.Return.SUCCESS;
                                })
                        )
        );
    }
}
