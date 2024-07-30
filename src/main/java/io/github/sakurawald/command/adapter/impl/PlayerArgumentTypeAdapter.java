package io.github.sakurawald.command.adapter.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.adapter.ArgumentTypeAdapter;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerArgumentTypeAdapter extends ArgumentTypeAdapter {

    public PlayerArgumentTypeAdapter() {
        super(ServerPlayerEntity.class);
    }

    @Override
    public ArgumentType<?> makeArgumentType() {
        return EntityArgumentType.player();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context) {
        return context.getSource().getPlayer();
    }

    @Override
    public boolean validateCommandSource(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            MessageHelper.sendMessage(context.getSource(), "command.player_only");
            return false;
        }

        return true;
    }
}
