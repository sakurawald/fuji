package io.github.sakurawald.module.initializer.command_toolbox.send_broadcast;

import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.auxiliary.minecraft.ServerHelper;
import net.minecraft.server.network.ServerPlayerEntity;

public class SendBroadcastInitializer extends ModuleInitializer {

    @Command("send-broadcast")
    @CommandPermission(level =  4)
    int sendBroadcast(GreedyString rest){
        String message = rest.getString();

        for (ServerPlayerEntity player : ServerHelper.getDefaultServer().getPlayerManager().getPlayerList()) {
            player.sendMessage(MessageHelper.ofText(player, false, message));
        }
        return CommandHelper.Return.SUCCESS;
    }

}
