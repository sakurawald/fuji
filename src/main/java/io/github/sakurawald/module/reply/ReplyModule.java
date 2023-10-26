package io.github.sakurawald.module.reply;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.ServerMain;
import io.github.sakurawald.module.AbstractModule;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

import static net.minecraft.commands.Commands.argument;

@Slf4j
public class ReplyModule extends AbstractModule {

    private final HashMap<String, String> player2target = new HashMap<>();

    public void updateReplyTarget(String player, String target) {
        this.player2target.put(player, target);
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
    }

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
            ServerMain.SERVER.getCommands().getDispatcher().execute("msg %s %s".formatted(target, message), player.createCommandSourceStack());
        } catch (CommandSyntaxException e) {
            log.error(e.getMessage());
        }

        return Command.SINGLE_SUCCESS;
    }

}
