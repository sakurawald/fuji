package io.github.sakurawald.module.initializer.command_toolbox.send_actionbar;

import com.mojang.brigadier.CommandDispatcher;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

public class SendActionbar extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("sendactionbar")
                        .requires(ctx -> ctx.hasPermissionLevel(4))
                        .then(CommandHelper.Argument.player()
                                .then(CommandHelper.Argument.rest()
                                        .executes((ctx) -> {
                                            ServerPlayerEntity player = CommandHelper.Argument.player(ctx);
                                            String message = CommandHelper.Argument.rest(ctx);

                                            player.sendActionBar(MessageHelper.ofText(player, false, message));
                                            return CommandHelper.Return.SUCCESS;
                                        })
                                )
                        )
        );
    }

}
