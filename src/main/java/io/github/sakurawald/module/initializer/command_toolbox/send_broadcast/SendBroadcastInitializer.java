package io.github.sakurawald.module.initializer.command_toolbox.send_broadcast;

import com.mojang.brigadier.CommandDispatcher;
import io.github.sakurawald.command.adapter.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.literal;

public class SendBroadcastInitializer extends ModuleInitializer {

    @Command("sendbroadcast")
    @CommandPermission(level =  4)
    int sendBroadcast(GreedyString rest){
        String message = rest.getString();

        for (ServerPlayerEntity player : ServerHelper.getDefaultServer().getPlayerManager().getPlayerList()) {
            player.sendMessage(MessageHelper.ofText(player, false, message));
        }
        return CommandHelper.Return.SUCCESS;
    }

}
