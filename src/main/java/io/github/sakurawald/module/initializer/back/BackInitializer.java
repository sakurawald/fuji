package io.github.sakurawald.module.initializer.back;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.common.structure.Position;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.HashMap;

import static net.minecraft.server.command.CommandManager.literal;

@SuppressWarnings("LombokGetterMayBeUsed")
public class BackInitializer extends ModuleInitializer {

    @Getter
    private final HashMap<String, Position> player2lastPos = new HashMap<>();

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("back").executes(this::$back));
    }

    private int $back(CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.playerOnlyCommand(ctx, (player -> {
            Position lastPos = player2lastPos.get(player.getName().getString());
            if (lastPos == null) {
                MessageHelper.sendActionBar(player, "back.no_previous_position");
                return CommandHelper.Return.ERROR;
            }

            lastPos.teleport(player);
            return CommandHelper.Return.SUCCESS;
        }));
    }

    public void saveCurrentPosition(ServerPlayerEntity player) {
        Position lastPos = player2lastPos.get(player.getGameProfile().getName());
        double ignoreDistance = Configs.configHandler.model().modules.back.ignore_distance;
        if (lastPos == null
                || (!lastPos.sameLevel(player.getWorld()))
                || (lastPos.sameLevel(player.getWorld()) && player.getPos().squaredDistanceTo(lastPos.getX(), lastPos.getY(), lastPos.getZ()) > ignoreDistance * ignoreDistance)
        ) {
            player2lastPos.put(player.getGameProfile().getName(),
                    Position.of(player));
        }
    }

}
