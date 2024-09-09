package io.github.sakurawald.module.initializer.back;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.structure.Position;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.Getter;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@SuppressWarnings("LombokGetterMayBeUsed")
public class BackInitializer extends ModuleInitializer {

    @Getter
    private final HashMap<String, Position> player2lastPos = new HashMap<>();

    @CommandNode("back")
    private int $back(@CommandSource ServerPlayerEntity player) {
        Position lastPos = player2lastPos.get(player.getName().getString());
        if (lastPos == null) {
            LocaleHelper.sendActionBarByKey(player, "back.no_previous_position");
            return CommandHelper.Return.FAIL;
        }

        lastPos.teleport(player);
        return CommandHelper.Return.SUCCESS;
    }

    public void saveCurrentPosition(@NotNull ServerPlayerEntity player) {
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
