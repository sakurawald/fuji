package io.github.sakurawald.module.initializer.reply;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

import static net.minecraft.commands.Commands.argument;


public class ReplyModule extends ModuleInitializer {

    private final HashMap<String, String> player2target = new HashMap<>();

    public void updateReplyTarget(String player, String target) {
        this.player2target.put(player, target);
    }


    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("reply").then(argument("message", StringArgumentType.greedyString()).executes(this::$reply)));
    }

    @SuppressWarnings("SameReturnValue")
    private int $reply(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        String target = this.player2target.get(player.getGameProfile().getName());
        String message = StringArgumentType.getString(ctx, "message");

        try {
            Fuji.SERVER.getCommands().getDispatcher().execute("msg %s %s".formatted(target, message), player.createCommandSourceStack());
        } catch (CommandSyntaxException e) {
            MessageUtil.sendMessage(player, "reply.no_target");
        }

        return Command.SINGLE_SUCCESS;
    }

}
