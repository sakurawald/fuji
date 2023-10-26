package io.github.sakurawald.module.back;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.module.teleport_warmup.Position;
import io.github.sakurawald.util.MessageUtil;
import lombok.Getter;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

@SuppressWarnings("LombokGetterMayBeUsed")
public class BackModule extends AbstractModule {

    @Getter
    private final HashMap<String, Position> player2lastPos = new HashMap<>();

    @SuppressWarnings("unused")
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("back").executes(this::$back));
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
    }

    private int $back(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        Position lastPos = player2lastPos.get(player.getName().getString());
        if (lastPos == null) {
            MessageUtil.sendActionBar(player, "back.no_previous_position");
            return Command.SINGLE_SUCCESS;
        }

        player.teleportTo((ServerLevel) lastPos.getLevel(), lastPos.getX(), lastPos.getY(), lastPos.getZ(), lastPos.getYaw(), lastPos.getPitch());
        return Command.SINGLE_SUCCESS;
    }

    public void updatePlayer(ServerPlayer player) {
        Position lastPos = player2lastPos.get(player.getGameProfile().getName());
        double ignoreDistance = ConfigManager.configWrapper.instance().modules.back.ignore_distance;
        if (lastPos == null
                || (player.level() != lastPos.getLevel())
                || (player.level() == lastPos.getLevel() && player.position().distanceToSqr(lastPos.getX(), lastPos.getY(), lastPos.getZ()) > ignoreDistance * ignoreDistance)
        ) {
            player2lastPos.put(player.getGameProfile().getName(),
                    Position.of(player));
        }
    }

}
