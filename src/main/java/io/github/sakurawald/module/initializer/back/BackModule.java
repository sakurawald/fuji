package io.github.sakurawald.module.initializer.back;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.teleport_warmup.Position;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import lombok.Getter;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

@SuppressWarnings("LombokGetterMayBeUsed")
public class BackModule extends ModuleInitializer {

    @Getter
    private final HashMap<String, Position> player2lastPos = new HashMap<>();

    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("back").executes(this::$back));
    }

    private int $back(CommandContext<CommandSourceStack> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, (player -> {
            Position lastPos = player2lastPos.get(player.getName().getString());
            if (lastPos == null) {
                MessageUtil.sendActionBar(player, "back.no_previous_position");
                return Command.SINGLE_SUCCESS;
            }

            lastPos.teleport(player);
            return Command.SINGLE_SUCCESS;
        }));
    }

    public void updatePlayer(ServerPlayer player) {
        Position lastPos = player2lastPos.get(player.getGameProfile().getName());
        double ignoreDistance = Configs.configHandler.model().modules.back.ignore_distance;
        if (lastPos == null
                || (!lastPos.sameLevel(player.level()))
                || (lastPos.sameLevel(player.level()) && player.position().distanceToSqr(lastPos.getX(), lastPos.getY(), lastPos.getZ()) > ignoreDistance * ignoreDistance)
        ) {
            player2lastPos.put(player.getGameProfile().getName(),
                    Position.of(player));
        }
    }

}
