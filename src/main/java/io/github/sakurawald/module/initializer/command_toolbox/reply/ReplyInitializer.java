package io.github.sakurawald.module.initializer.command_toolbox.reply;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.auxiliary.minecraft.ServerHelper;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;


public class ReplyInitializer extends ModuleInitializer {

    private final HashMap<String, String> player2target = new HashMap<>();

    public void updateReplyTarget(String player, String target) {
        this.player2target.put(player, target);
    }


    @CommandNode("reply")
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
