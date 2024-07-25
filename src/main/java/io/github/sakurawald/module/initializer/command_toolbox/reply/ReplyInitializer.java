package io.github.sakurawald.module.initializer.command_toolbox.reply;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import java.util.HashMap;

import io.github.sakurawald.util.minecraft.ServerHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;


public class ReplyInitializer extends ModuleInitializer {

    private final HashMap<String, String> player2target = new HashMap<>();

    public void updateReplyTarget(String player, String target) {
        this.player2target.put(player, target);
    }


    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("reply").then(argument("message", StringArgumentType.greedyString()).executes(this::$reply)));
    }

    @SuppressWarnings("SameReturnValue")
    private int $reply(CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, player -> {

            String target = this.player2target.get(player.getGameProfile().getName());
            String message = StringArgumentType.getString(ctx, "message");

            try {
                ServerHelper.getDefaultServer().getCommandManager().getDispatcher().execute("msg %s %s".formatted(target, message), player.getCommandSource());
            } catch (CommandSyntaxException e) {
                MessageHelper.sendMessage(player, "reply.no_target");
            }

            return CommandHelper.Return.SUCCESS;
        });
    }

}
