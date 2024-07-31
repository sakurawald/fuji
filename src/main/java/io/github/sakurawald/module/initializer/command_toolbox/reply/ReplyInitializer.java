package io.github.sakurawald.module.initializer.command_toolbox.reply;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.command.adapter.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static net.minecraft.server.command.CommandManager.argument;


public class ReplyInitializer extends ModuleInitializer {

    private final HashMap<String, String> player2target = new HashMap<>();

    public void updateReplyTarget(String player, String target) {
        this.player2target.put(player, target);
    }


    @Command("reply")
    private int $reply(@CommandSource ServerPlayerEntity player, GreedyString message) {
        String target = this.player2target.get(player.getGameProfile().getName());

        try {
            ServerHelper.getDefaultServer().getCommandManager().getDispatcher().execute("msg %s %s".formatted(target, message.getString()), player.getCommandSource());
        } catch (CommandSyntaxException e) {
            MessageHelper.sendMessage(player, "reply.no_target");
        }

        return CommandHelper.Return.SUCCESS;
    }

}
