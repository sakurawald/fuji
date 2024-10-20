package io.github.sakurawald.module.initializer.back;

import io.github.sakurawald.core.annotation.Document;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.TextHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.structure.SpatialPose;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.back.config.model.BackConfigModel;
import lombok.Getter;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BackInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<BackConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, BackConfigModel.class);

    @Getter
    private static final HashMap<String, SpatialPose> player2lastPos = new HashMap<>();

    @CommandNode("back")
    @Document("Back to the recent death location or recent teleport location.")
    private static int $back(@CommandSource ServerPlayerEntity player) {
        SpatialPose lastPos = player2lastPos.get(player.getName().getString());
        if (lastPos == null) {
            TextHelper.sendActionBarByKey(player, "back.no_previous_position");
            return CommandHelper.Return.FAIL;
        }

        lastPos.teleport(player);
        return CommandHelper.Return.SUCCESS;
    }

    public static void saveCurrentPosition(@NotNull ServerPlayerEntity player) {
        SpatialPose lastPos = player2lastPos.get(player.getGameProfile().getName());
        double ignoreDistance = config.model().ignore_distance;
        if (lastPos == null
            || !lastPos.sameLevel(player.getWorld())
            || lastPos.sameLevel(player.getWorld()) && player.getPos().squaredDistanceTo(lastPos.getX(), lastPos.getY(), lastPos.getZ()) > ignoreDistance * ignoreDistance
        ) {
            player2lastPos.put(player.getGameProfile().getName(),
                SpatialPose.of(player));
        }
    }

}
