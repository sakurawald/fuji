package io.github.sakurawald.module.initializer.command_meta.for_each;

import com.mojang.brigadier.CommandDispatcher;
import io.github.sakurawald.module.common.structure.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

public class ForEachInitializer extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {

        dispatcher.register(
                literal("foreach")
                        .requires(ctx -> ctx.hasPermissionLevel(4))
                        .then(CommandHelper.Argument.rest()
                                .executes((ctx) -> {
                                    String rest = CommandHelper.Argument.rest(ctx);
                                    MinecraftServer server = ctx.getSource().getServer();

                                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                        CommandExecutor.executeCommandAsConsole(player, rest);
                                    }
                                    return CommandHelper.Return.SUCCESS;
                                })
                        ));
    }
}
